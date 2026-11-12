package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.CartItemResponseDTO;
import com.example.jler_chinese_system.dto.CartResponseDTO;
import com.example.jler_chinese_system.entity.Book;
import com.example.jler_chinese_system.entity.BookImage;
import com.example.jler_chinese_system.entity.Cart;
import com.example.jler_chinese_system.entity.CartItem;
import com.example.jler_chinese_system.entity.Course;
import com.example.jler_chinese_system.entity.User;
import com.example.jler_chinese_system.repository.BookRepository;
import com.example.jler_chinese_system.repository.CartItemRepository;
import com.example.jler_chinese_system.repository.CartRepository;
import com.example.jler_chinese_system.repository.ChapterRepository;
import com.example.jler_chinese_system.repository.CourseRepository;
import com.example.jler_chinese_system.repository.UserRepository;
import com.example.jler_chinese_system.service.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final CourseRepository courseRepository;

  @Override
  public void addToCart(Integer userId, Integer itemId, String itemType) {
    Cart cart = cartRepository
      .findByUser_UserId(userId)
      .orElseGet(() -> createNewCart(userId));

    // Kiểm tra nếu item đã tồn tại thì tăng quantity
    Optional<CartItem> existingItem = cart
      .getCartItems()
      .stream()
      .filter(
        i ->
          i.getItemId().equals(itemId) &&
          i.getItemType().equalsIgnoreCase(itemType)
      )
      .findFirst();

    if (existingItem.isPresent()) {
      existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
    } else {
      BigDecimal price;

      if (itemType.equalsIgnoreCase("Book")) {
        Book book = bookRepository.findById(itemId).orElseThrow();
        price = book.getPrice();
      } else {
        Course course = courseRepository.findById(itemId).orElseThrow();
        price = course.getPrice();
      }

      CartItem newItem = new CartItem();
      newItem.setCart(cart);
      newItem.setItemId(itemId);
      newItem.setItemType(itemType);
      newItem.setQuantity(1);
      newItem.setPriceAtPurchase(price);

      cart.getCartItems().add(newItem);
    }

    cartRepository.save(cart);
  }

  private Cart createNewCart(Integer userId) {
    User user = userRepository.findById(userId).orElseThrow();

    Cart cart = new Cart();
    cart.setUser(user);

    return cartRepository.save(cart);
  }

  @Override
  public void updateQuantity(
    Integer userId,
    Integer cartItemId,
    Integer quantity
  ) {
    CartItem item = cartItemRepository.findById(cartItemId).orElseThrow();

    if (quantity <= 0) {
      cartItemRepository.delete(item);
    } else {
      item.setQuantity(quantity);
    }
  }

  @Override
  public void removeItem(Integer userId, Integer cartItemId) {
    cartItemRepository.deleteById(cartItemId);
  }

  @Override
  public CartResponseDTO getCart(Integer userId) {
    Cart cart = cartRepository.findByUser_UserId(userId).orElseThrow();

    List<CartItemResponseDTO> itemDTOs = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;
    int totalItems = 0;

    for (CartItem item : cart.getCartItems()) {
      String name = "";
      String image = "";

      if (item.getItemType().equalsIgnoreCase("Book")) {
        Book book = item.getBook();
        name = book.getTitle();
        image = book
          .getBookImages()
          .stream()
          .filter(img -> Boolean.TRUE.equals(img.getIsThumbNail()))
          .map(BookImage::getImageUrl)
          .findFirst()
          .orElse(
            book
              .getBookImages()
              .stream()
              .findFirst()
              .map(BookImage::getImageUrl)
              .orElse(null)
          );
      } else {
        Course course = item.getCourse();
        name = course.getTitle();
        image = course.getThumbnailUrl();
      }

      BigDecimal total = item
        .getPriceAtPurchase()
        .multiply(BigDecimal.valueOf(item.getQuantity()));

      totalAmount = totalAmount.add(total);
      totalItems += item.getQuantity();

      itemDTOs.add(
        CartItemResponseDTO.builder()
          .cartItemId(item.getCartItemId())
          .itemId(item.getItemId())
          .itemType(item.getItemType())
          .itemName(name)
          .thumbnail(image)
          .quantity(item.getQuantity())
          .price(item.getPriceAtPurchase())
          .totalPrice(total)
          .build()
      );
    }

    return CartResponseDTO.builder()
      .cartId(cart.getCartId())
      .totalItems(totalItems)
      .totalAmount(totalAmount)
      .items(itemDTOs)
      .build();
  }

  @Override
  public Integer getCartCount(Integer userId) {
    return getCart(userId).getTotalItems();
  }
}
