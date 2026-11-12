package com.example.jler_chinese_system.service.impl;

import static com.example.jler_chinese_system.common.Checks.check;
import static com.example.jler_chinese_system.common.ResponseMaps.map;
import static com.example.jler_chinese_system.mapper.PlatformMapper.*;
import static com.example.jler_chinese_system.utils.RequestValues.*;

import com.example.jler_chinese_system.entity.*;
import com.example.jler_chinese_system.mapper.PlatformMapper;
import com.example.jler_chinese_system.repository.UserRepository;
import com.example.jler_chinese_system.security.CurrentUser;
import com.example.jler_chinese_system.service.*;
import com.example.jler_chinese_system.service.support.*;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingServiceImpl implements ShoppingService {

  @PersistenceContext
  private EntityManager em;

  private final EntityLookup data;
  private final CurrentUser currentUser;
  private final LearningAccessService learningAccess;
  private final SePayService sepay;
  private final OrderPaymentService payments;

  private Cart cartEntity() {
    User u = currentUser.get();
    em.lock(u, LockModeType.PESSIMISTIC_WRITE);
    var carts = em
      .createQuery("from Cart c where c.user=:u", Cart.class)
      .setParameter("u", u)
      .getResultList();
    Cart c;
    if (carts.isEmpty()) {
      c = new Cart();
      c.setUser(u);
      data.save(c);
    } else c = carts.get(0);
    // Normalize old case-insensitive item types and merge duplicate legacy rows.
    var canonical = new LinkedHashMap<String, CartItem>();
    for (var item : new ArrayList<>(c.getCartItems())) {
      String type = canonicalType(item.getItemType());
      item.setItemType(type);
      if (!Set.of("Book", "Course").contains(type)) continue;
      if (type.equals("Course")) item.setQuantity(1);
      var key = type + ":" + item.getItemId();
      var first = canonical.putIfAbsent(key, item);
      if (first != null) {
        if (type.equals("Book")) first.setQuantity(
          (int) Math.min(
            Integer.MAX_VALUE,
            (long) Math.max(0, first.getQuantity()) +
              Math.max(0, item.getQuantity())
          )
        );
        c.getCartItems().remove(item);
      }
    }
    return c;
  }

  private BigDecimal itemPrice(String type, Integer id) {
    if (type.equals("Book")) {
      Book b = data.find(Book.class, id);
      check(b.getStock() != null && b.getStock() > 0, "Sách đã hết hàng");
      return price(b);
    }
    Course c = data.find(Course.class, id);
    check(!"HIDDEN".equals(c.getStatus()), "Khóa học đã ẩn");
    check(
      !learningAccess.enrolled(currentUser.get(), id),
      "Bạn đã sở hữu khóa học này"
    );
    return price(c);
  }

  public Map<String, Object> addCart(Map<String, Object> d) {
    Cart c = cartEntity();
    String type = itemType(required(d, "itemType"));
    int id = number(d, "itemId", 0);
    BigDecimal price = itemPrice(type, id);
    int amount = number(d, "quantity", 1);
    check(amount > 0 && amount <= 99, "Số lượng từ 1 đến 99");
    var item = c
      .getCartItems()
      .stream()
      .filter(
        x -> type.equals(x.getItemType()) && Objects.equals(x.getItemId(), id)
      )
      .findFirst()
      .orElse(null);
    if (item == null) {
      item = new CartItem();
      item.setCart(c);
      item.setItemId(id);
      item.setItemType(type);
      item.setQuantity(0);
      c.getCartItems().add(item);
    }
    int quantity = type.equals("Course") ? 1 : item.getQuantity() + amount;
    check(quantity <= 99, "Tối đa 99 cuốn mỗi đầu sách");
    if (type.equals("Book")) check(
      quantity <= data.find(Book.class, id).getStock(),
      "Không đủ tồn kho"
    );
    item.setQuantity(quantity);
    item.setPriceAtPurchase(price);
    em.flush();
    return cartView(c);
  }

  private CartItem ownedItem(Cart c, Integer id) {
    return c
      .getCartItems()
      .stream()
      .filter(x -> Objects.equals(x.getCartItemId(), id))
      .findFirst()
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Không tìm thấy mục giỏ hàng"
        )
      );
  }

  private void setQuantity(Cart c, CartItem item, int quantity) {
    if (quantity == 0) {
      c.getCartItems().remove(item);
      return;
    }
    check(quantity > 0 && quantity <= 99, "Số lượng từ 1 đến 99");
    String type = itemType(item.getItemType());
    check(
      !type.equals("Course") || quantity == 1,
      "Khóa học chỉ cần mua một lần cho mỗi tài khoản"
    );
    if (type.equals("Book")) check(
      quantity <=
        Objects.requireNonNullElse(
          data.find(Book.class, item.getItemId()).getStock(),
          0
        ),
      "Số lượng vượt quá tồn kho hiện tại"
    );
    item.setQuantity(quantity);
  }

  public Map<String, Object> changeCart(Integer id, Integer quantity) {
    check(quantity != null, "Thiếu số lượng");
    var c = cartEntity();
    setQuantity(c, ownedItem(c, id), quantity);
    em.flush();
    return cartView(c);
  }

  public Map<String, Object> adjustCart(Integer id, int delta) {
    check(delta == 1 || delta == -1, "Chỉ tăng hoặc giảm một đơn vị");
    var c = cartEntity();
    var item = ownedItem(c, id);
    check(
      "Book".equals(item.getItemType()),
      "Khóa học chỉ cần mua một lần cho mỗi tài khoản"
    );
    int next = item.getQuantity() + delta;
    check(next > 0, "Số lượng tối thiểu là 1; dùng Xóa để bỏ sản phẩm");
    if (delta < 0) item.setQuantity(next);
    else setQuantity(c, item, next);
    em.flush();
    return cartView(c);
  }

  public Map<String, Object> clearCart() {
    var c = cartEntity();
    c.getCartItems().clear();
    em.flush();
    return cartView(c);
  }

  public Map<String, Object> cart() {
    return cartView(cartEntity());
  }

  private Map<String, Object> cartView(Cart c) {
    var items = new ArrayList<Map<String, Object>>();
    BigDecimal subtotal = BigDecimal.ZERO;
    int totalItems = 0;
    boolean valid = true;
    for (var item : c.getCartItems()) {
      String type = canonicalType(item.getItemType()),
        title = "Sản phẩm không còn khả dụng",
        image = "",
        reason = "";
      BigDecimal unit = Objects.requireNonNullElse(
        item.getPriceAtPurchase(),
        BigDecimal.ZERO
      );
      int maximum = 0;
      boolean available = false;
      if (type.equals("Book")) {
        Book b = em.find(Book.class, item.getItemId());
        if (b != null) {
          title = b.getTitle();
          unit = price(b);
          image = Objects.toString(bookView(b).get("imageUrl"), "");
          maximum = Math.min(
            99,
            Math.max(0, Objects.requireNonNullElse(b.getStock(), 0))
          );
          available = maximum > 0;
          reason = available ? "" : "Sách tạm hết hàng";
        }
      } else if (type.equals("Course")) {
        Course course = em.find(Course.class, item.getItemId());
        if (course != null) {
          title = course.getTitle();
          unit = price(course);
          image = Objects.toString(course.getThumbnailUrl(), "");
          maximum = 1;
          available =
            !"HIDDEN".equals(course.getStatus()) &&
            !learningAccess.enrolled(c.getUser(), item.getItemId());
          reason = available
            ? ""
            : learningAccess.enrolled(c.getUser(), item.getItemId())
              ? "Bạn đã sở hữu khóa học này"
              : "Khóa học ngừng bán";
        }
      }
      if (!available && reason.isBlank()) reason =
        "Sản phẩm đã bị gỡ; vui lòng xóa khỏi giỏ";
      int quantity = Objects.requireNonNullElse(item.getQuantity(), 1);
      if (quantity > maximum && available) {
        available = false;
        reason = "Chỉ còn " + maximum + " cuốn; vui lòng điều chỉnh số lượng";
      }
      if (quantity <= 0) {
        available = false;
        reason = "Số lượng không hợp lệ; vui lòng điều chỉnh";
      }
      BigDecimal total = unit.multiply(BigDecimal.valueOf(quantity));
      subtotal = subtotal.add(total);
      totalItems += quantity;
      valid &= available;
      items.add(
        map(
          "id",
          item.getCartItemId(),
          "itemId",
          item.getItemId(),
          "itemType",
          type,
          "title",
          title,
          "imageUrl",
          image,
          "price",
          unit,
          "quantity",
          quantity,
          "lineTotal",
          total,
          "maxQuantity",
          maximum,
          "available",
          available,
          "availabilityReason",
          reason
        )
      );
    }
    return map(
      "items",
      items,
      "subtotal",
      subtotal,
      "totalItems",
      totalItems,
      "canCheckout",
      !items.isEmpty() && valid
    );
  }

  private Voucher voucher(String code, BigDecimal subtotal) {
    if (code == null || code.isBlank()) return null;
    var list = em
      .createQuery("from Voucher v where v.code=:c", Voucher.class)
      .setParameter("c", code.toUpperCase(Locale.ROOT).trim())
      .setLockMode(LockModeType.PESSIMISTIC_WRITE)
      .getResultList();
    check(!list.isEmpty(), "Mã giảm giá không tồn tại");
    var v = list.get(0);
    check(
      Boolean.TRUE.equals(v.getActive()) &&
        (v.getExpiredAt() == null ||
          !v.getExpiredAt().isBefore(LocalDate.now())) &&
        v.getUsedCount() < v.getMaxUsage(),
      "Mã giảm giá hết hạn hoặc hết lượt"
    );
    check(
      subtotal.compareTo(v.getMinOrder()) >= 0,
      "Đơn chưa đạt giá trị tối thiểu"
    );
    return v;
  }

  private BigDecimal discount(Voucher v, BigDecimal sub) {
    if (v == null) return BigDecimal.ZERO;
    BigDecimal d = v.getType().equals("PERCENT")
      ? sub
          .multiply(v.getDiscount())
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
      : v.getDiscount();
    if (v.getMaxDiscount() != null) d = d.min(v.getMaxDiscount());
    return d.min(sub);
  }

  public Map<String, Object> quote(String code) {
    var c = cart();
    BigDecimal sub = (BigDecimal) c.get("subtotal");
    var v = voucher(code, sub);
    BigDecimal disc = discount(v, sub);
    boolean physical = ((List<?>) c.get("items"))
      .stream()
      .anyMatch(x -> "Book".equals(((Map<?, ?>) x).get("itemType")));
    BigDecimal ship = physical ? BigDecimal.valueOf(30000) : BigDecimal.ZERO;
    return map(
      "subtotal",
      sub,
      "discount",
      disc,
      "shippingFee",
      ship,
      "total",
      sub.subtract(disc).add(ship)
    );
  }

  public Map<String, Object> checkout(Map<String, Object> d) {
    Cart cart = cartEntity();
    String requestKey = text(d, "checkoutKey"),
      signature = checkoutSignature(d);
    if (!requestKey.isBlank()) {
      check(
        requestKey.matches("[A-Za-z0-9-]{16,64}"),
        "Mã yêu cầu đặt hàng không hợp lệ"
      );
      var existing = em
        .createQuery(
          "from Order o where o.user=:u and o.checkoutKey=:k",
          Order.class
        )
        .setParameter("u", cart.getUser())
        .setParameter("k", requestKey)
        .getResultList();
      if (!existing.isEmpty()) {
        Order previous = existing.get(0);
        if (
          !Objects.equals(previous.getRequestSignature(), signature)
        ) throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Yêu cầu đặt hàng đã thay đổi; hãy thử lại với yêu cầu mới"
        );
        return orderView(previous);
      }
    }
    check(!cart.getCartItems().isEmpty(), "Giỏ hàng trống");
    check(
      Boolean.TRUE.equals(cartView(cart).get("canCheckout")),
      "Giỏ có sản phẩm hết hàng, đã sở hữu hoặc số lượng không hợp lệ"
    );
    String method = text(d, "paymentMethod");
    check(
      Set.of("COD", "BANK_TRANSFER", "SEPAY", "FREE").contains(method),
      "Phương thức thanh toán không hợp lệ"
    );
    if (method.equals("SEPAY")) sepay.requireConfigured();
    boolean physical = cart
      .getCartItems()
      .stream()
      .anyMatch(i -> "Book".equals(i.getItemType()));
    check(
      physical ||
        method.equals("BANK_TRANSFER") ||
        method.equals("SEPAY") ||
        method.equals("FREE") ||
        ((BigDecimal) cart().get("subtotal")).signum() == 0,
      "Khóa học cần thanh toán trực tuyến"
    );
    Order o = new Order();
    o.setUser(currentUser.get());
    o.setCheckoutKey(requestKey.isBlank() ? null : requestKey);
    o.setRequestSignature(signature);
    o.setOrderCode(
      "JL" +
        UUID.randomUUID()
          .toString()
          .replace("-", "")
          .substring(0, 16)
          .toUpperCase()
    );
    o.setNote(text(d, "note"));
    o.setPaymentMethod(method);
    o.setHasPhysicalItem(physical);
    if (physical) {
      ShippingOder s = new ShippingOder();
      s.setOrder(o);
      s.setFullName(required(d, "fullName"));
      s.setPhone(required(d, "phone"));
      check(
        s.getPhone().matches("[+0-9 .-]{8,15}"),
        "Số điện thoại không hợp lệ"
      );
      s.setAddress(required(d, "address"));
      o.setShippingOrder(s);
      o.setShippingStatus(Order.ShippingStatus.preparing);
    }
    // Stable lock order avoids deadlocks across multi-book checkouts.
    var sorted = cart
      .getCartItems()
      .stream()
      .sorted(
        Comparator.comparing(CartItem::getItemType).thenComparing(
          CartItem::getItemId
        )
      )
      .toList();
    for (var i : sorted) {
      BigDecimal p = itemPrice(i.getItemType(), i.getItemId());
      String title;
      if (i.getItemType().equals("Book")) {
        Book b = data.find(Book.class, i.getItemId());
        em.lock(b, LockModeType.PESSIMISTIC_WRITE);
        em.refresh(b);
        check(
          b.getStock() >= i.getQuantity(),
          "Không đủ tồn kho: " + b.getTitle()
        );
        b.setStock(b.getStock() - i.getQuantity());
        p = price(b);
        title = b.getTitle();
      } else title = data.find(Course.class, i.getItemId()).getTitle();
      var oi = new OrderItem();
      oi.setOrder(o);
      oi.setItemId(i.getItemId());
      oi.setItemType(OrderItem.ItemType.valueOf(i.getItemType()));
      oi.setQuantity(i.getQuantity());
      oi.setPriceAtPurchase(p);
      oi.setItemTitle(title);
      o.getOderItems().add(oi);
      o.setSubtotal(
        o.getSubtotal().add(p.multiply(BigDecimal.valueOf(i.getQuantity())))
      );
    }
    var v = voucher(text(d, "voucherCode"), o.getSubtotal());
    if (v != null) {
      v.setUsedCount(v.getUsedCount() + 1);
      o.setVoucherId(v.getId());
    }
    o.setDiscount(discount(v, o.getSubtotal()));
    o.setShippingFee(physical ? BigDecimal.valueOf(30000) : BigDecimal.ZERO);
    o.setTotalAmount(
      o.getSubtotal().subtract(o.getDiscount()).add(o.getShippingFee())
    );
    if (method.equals("FREE")) check(
      o.getTotalAmount().signum() == 0,
      "Đơn hàng này cần thanh toán"
    );
    if (method.equals("SEPAY")) check(
      o.getTotalAmount().stripTrailingZeros().scale() <= 0,
      "Thanh toán SePay yêu cầu số tiền nguyên đồng"
    );
    data.save(o);
    cart.getCartItems().clear();
    if (o.getTotalAmount().signum() == 0) {
      payments.complete(o, "FREE");
    }
    em.flush();
    return orderView(o);
  }

  private String checkoutSignature(Map<String, Object> d) {
    try {
      StringBuilder b = new StringBuilder();
      for (String key : List.of(
        "paymentMethod",
        "fullName",
        "phone",
        "address",
        "voucherCode",
        "note"
      )) {
        String v = text(d, key);
        b.append(key).append(':').append(v.length()).append(':').append(v);
      }
      return java.util.HexFormat.of().formatHex(
        java.security.MessageDigest.getInstance("SHA-256").digest(
          b.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)
        )
      );
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public Map<String, Object> orderDetail(Integer id) {
    var o = data.find(Order.class, id);
    check(
      currentUser.isAdmin() ||
        o.getUser().getUserId().equals(currentUser.get().getUserId()),
      "Đơn hàng không thuộc tài khoản của bạn"
    );
    return orderView(o);
  }

  public List<?> orders(boolean administrator) {
    return (
      administrator
        ? data.all(Order.class)
        : em
            .createQuery(
              "from Order o where o.user.userId=:u order by o.createdAt desc",
              Order.class
            )
            .setParameter("u", currentUser.get().getUserId())
            .getResultList()
    )
      .stream()
      .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
      .map(PlatformMapper::orderView)
      .toList();
  }

  public Map<String, Object> orderAction(
    Integer id,
    Map<String, Object> d,
    boolean administrator
  ) {
    Order o = data.find(Order.class, id);
    em.lock(o, LockModeType.PESSIMISTIC_WRITE);
    String action = required(d, "action");
    if (!administrator) check(
      o.getUser().getUserId().equals(currentUser.get().getUserId()),
      "Đơn hàng không thuộc tài khoản của bạn"
    );
    if (action.equals("CANCEL")) {
      check(
        o.getPaymentStatus() != Order.PaymentStatus.paid &&
          o.getStatus().equals("PENDING"),
        "Chỉ hủy đơn chờ xử lý, chưa thanh toán"
      );
      o.setStatus("CANCELED");
      o.setShippingStatus(Order.ShippingStatus.cancelled);
      for (var i : o
        .getOderItems()
        .stream()
        .sorted(Comparator.comparing(OrderItem::getItemId))
        .toList())
        if (i.getItemType() == OrderItem.ItemType.Book) {
          var b = data.find(Book.class, i.getItemId());
          em.lock(b, LockModeType.PESSIMISTIC_WRITE);
          em.refresh(b);
          b.setStock(b.getStock() + i.getQuantity());
        }
      if (o.getVoucherId() != null) {
        Voucher v = em.find(
          Voucher.class,
          o.getVoucherId(),
          LockModeType.PESSIMISTIC_WRITE
        );
        if (v != null) v.setUsedCount(Math.max(0, v.getUsedCount() - 1));
      }
    } else {
      check(administrator, "Bạn không có quyền cập nhật đơn");
      check(
        !Set.of("CANCELED", "COMPLETED").contains(o.getStatus()),
        "Đơn đã kết thúc"
      );
      if (action.equals("MARK_PAID")) {
        check(
          o.getPaymentStatus() != Order.PaymentStatus.paid,
          "Đơn đã thanh toán"
        );
        payments.complete(o, "MANUAL:" + currentUser.get().getUserId());
      } else if (action.equals("CONFIRM")) {
        check(o.getStatus().equals("PENDING"), "Trạng thái không hợp lệ");
        o.setStatus("PREPARING");
      } else if (action.equals("SHIP")) {
        check(
          o.getHasPhysicalItem() && o.getStatus().equals("PREPARING"),
          "Đơn chưa sẵn sàng giao"
        );
        o.setStatus("SHIPPING");
        o.setShippingStatus(Order.ShippingStatus.shipping);
      } else if (action.equals("COMPLETE")) {
        check(
          o.getStatus().equals("SHIPPING") &&
            o.getPaymentStatus() == Order.PaymentStatus.paid,
          "Cần giao hàng và xác nhận thanh toán trước"
        );
        o.setStatus("COMPLETED");
        o.setShippingStatus(Order.ShippingStatus.delivered);
      } else check(false, "Thao tác không hợp lệ");
    }
    return orderView(o);
  }

  public List<?> favorites() {
    return em
      .createQuery("from Favorite f where f.userId=:u", Favorite.class)
      .setParameter("u", currentUser.get().getUserId())
      .getResultList()
      .stream()
      .map(f -> {
        Map<String, Object> p = f.getItemType().equals("Book")
          ? bookView(data.find(Book.class, f.getItemId()))
          : courseView(data.find(Course.class, f.getItemId()));
        return map(
          "id",
          f.getId(),
          "itemId",
          f.getItemId(),
          "itemType",
          f.getItemType(),
          "product",
          p
        );
      })
      .toList();
  }

  public void favorite(Map<String, Object> d) {
    String t = itemType(required(d, "itemType"));
    int id = number(d, "itemId", 0);
    if (t.equals("Book")) data.find(Book.class, id);
    else data.find(Course.class, id);
    User u = currentUser.get();
    em.lock(u, LockModeType.PESSIMISTIC_WRITE);
    var fs = em
      .createQuery(
        "from Favorite f where f.userId=:u and f.itemId=:i and f.itemType=:t",
        Favorite.class
      )
      .setParameter("u", u.getUserId())
      .setParameter("i", id)
      .setParameter("t", t)
      .getResultList();
    if (fs.isEmpty()) {
      var f = new Favorite();
      f.setUserId(u.getUserId());
      f.setItemId(id);
      f.setItemType(t);
      data.save(f);
    } else em.remove(fs.get(0));
  }

  public Map<String, Object> payment(Integer id) {
    Order o = data.find(Order.class, id);
    check(
      currentUser.isAdmin() ||
        o.getUser().getUserId().equals(currentUser.get().getUserId()),
      "Đơn hàng không thuộc tài khoản của bạn"
    );
    return sepay.info(o);
  }

  public Map<String, Object> paymentOptions() {
    return map("sepayEnabled", sepay.enabled());
  }
}
