package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.*;
import com.example.jler_chinese_system.dto.filter.BookFilter;
import com.example.jler_chinese_system.entity.Book;
import com.example.jler_chinese_system.entity.BookImage;
import com.example.jler_chinese_system.entity.Category;
import com.example.jler_chinese_system.repository.*;
import com.example.jler_chinese_system.service.*;
import com.example.jler_chinese_system.specification.BookSpecification;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final CategoryRepository categoryRepository;
  private final UploadService uploadService;
  //delete book
  private final CartItemRepository cartItemRepository;
  private final ComboItemRepository comboItemRepository;
  private final OrderItemRepository orderItemRepository;

  @Override
  @Transactional(readOnly = true)
  public List<BookListResponseDTO> getAllFiltered(
    Integer catId,
    BigDecimal min,
    BigDecimal max
  ) {
    return bookRepository
      .filterBooks(catId, min, max)
      .stream()
      .map(this::mapToCardDTO)
      .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public BookDetailResponseDTO getDetail(Integer id) {
    Book book = findBookOrThrow(id);
    return mapToDetailDTO(book);
  }

  @Override
  public BookDetailResponseDTO create(BookRequestDTO request) {
    Book book = new Book();
    book.setTitle(request.getTitle());
    book.setDescription(request.getDescription());
    book.setPrice(request.getPrice());
    book.setDiscountPrice(request.getDiscountPrice());
    book.setStock(request.getStock());

    // ===== Categories =====
    if (request.getCategoryIds() != null) {
      List<Category> categories = categoryRepository.findAllById(
        request.getCategoryIds()
      );
      book.setCategories(new HashSet<>(categories));
    }

    // ===== Images =====
    if (request.getImageUrls() != null) {
      for (String url : request.getImageUrls()) {
        BookImage image = new BookImage();
        image.setImageUrl(url);
        image.setIsThumbNail(true);
        image.setBook(book);

        book.getBookImages().add(image);
      }
    }

    Book saved = bookRepository.save(book);

    return mapToDetailDTO(saved);
  }

  @Override
  public BookDetailResponseDTO update(
    Integer id,
    BookUpdateRequestDTO request
  ) {
    Book book = findBookOrThrow(id);

    book.setTitle(request.getTitle());
    book.setDescription(request.getDescription());
    book.setPrice(request.getPrice());
    book.setDiscountPrice(request.getDiscountPrice());
    book.setStock(request.getStock());

    // ===== Categories =====
    if (request.getCategoryIds() != null) {
      List<Category> categories = categoryRepository.findAllById(
        request.getCategoryIds()
      );

      book.setCategories(new HashSet<>(categories));
    }

    // ===== Images =====
    if (request.getImageUrls() != null) {
      List<String> oldImageUrls = book
        .getBookImages()
        .stream()
        .map(BookImage::getImageUrl)
        .toList();
      for (String oldUrl : oldImageUrls) {
        if (!request.getImageUrls().contains(oldUrl)) {
          uploadService.deleteFile(oldUrl);
        }
      }
      book.getBookImages().clear();

      for (int i = 0; i < request.getImageUrls().size(); i++) {
        BookImage image = new BookImage();
        image.setImageUrl(request.getImageUrls().get(i));
        image.setBook(book);

        if (i == 0) {
          image.setIsThumbNail(true);
        }

        book.getBookImages().add(image);
      }
    }

    Book saved = bookRepository.save(book);

    return mapToDetailDTO(saved);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    Book book = findBookOrThrow(id);

    cartItemRepository.deleteByItemId(id);

    Set<String> imageUrls = book
      .getBookImages()
      .stream()
      .map(BookImage::getImageUrl)
      .collect(Collectors.toSet());

    for (String url : imageUrls) {
      uploadService.deleteFile(url);
    }
    comboItemRepository.deleteByItemId(id);
    orderItemRepository.deleteByItemId(id);
    bookRepository.delete(book);
  }

  private Book findBookOrThrow(Integer id) {
    return bookRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
  }

  private BookDetailResponseDTO mapToDetailDTO(Book b) {
    BookDetailResponseDTO dto = new BookDetailResponseDTO();
    dto.setBookId(b.getBookId());
    dto.setTitle(b.getTitle());
    dto.setDescription(b.getDescription());
    dto.setPrice(b.getPrice());
    dto.setDiscountPrice(b.getDiscountPrice());
    dto.setStock(b.getStock());

    dto.setImageUrls(
      b.getBookImages().stream().map(BookImage::getImageUrl).toList()
    );

    dto.setCategories(
      b
        .getCategories()
        .stream()
        .map(c -> {
          BookDetailResponseDTO.CategorySummaryDTO s =
            new BookDetailResponseDTO.CategorySummaryDTO();
          s.setCategoryId(c.getCategoryId());
          s.setNameCategory(c.getNameCategory());
          return s;
        })
        .toList()
    );

    return dto;
  }

  private BookListResponseDTO mapToCardDTO(Book b) {
    BookListResponseDTO dto = new BookListResponseDTO();
    dto.setBookId(b.getBookId());
    dto.setTitle(b.getTitle());
    dto.setPrice(b.getPrice());
    dto.setDiscountPrice(b.getDiscountPrice());

    dto.setCategoryNames(
      b.getCategories().stream().map(Category::getNameCategory).toList()
    );

    b.getBookImages()
      .stream()
      .filter(img -> Boolean.TRUE.equals(img.getIsThumbNail()))
      .findFirst()
      .ifPresent(img -> dto.setThumbnail(img.getImageUrl()));

    return dto;
  }

  @Override
  public Page<BookInfomationDTO> getAllBooks(Pageable pageable) {
    Pageable sortedPageable = PageRequest.of(
      pageable.getPageNumber(),
      pageable.getPageSize(),
      Sort.by(Sort.Direction.DESC, "bookId")
    );

    return bookRepository.findAll(sortedPageable).map(this::mapToDTO);
  }

  private BookInfomationDTO mapToDTO(Book book) {
    BookInfomationDTO dto = new BookInfomationDTO();

    dto.setId(book.getBookId());
    dto.setName(book.getTitle());
    dto.setPrice(book.getPrice());
    dto.setStock(book.getStock());

    // ===== Thumbnail =====
    String imageUrl = book
      .getBookImages()
      .stream()
      .sorted((a, b) ->
        Boolean.compare(
          Boolean.TRUE.equals(b.getIsThumbNail()),
          Boolean.TRUE.equals(a.getIsThumbNail())
        )
      )
      .map(BookImage::getImageUrl)
      .findFirst()
      .orElse(null);

    dto.setImage(imageUrl);

    // ===== Multiple Categories =====
    List<String> categories = book
      .getCategories()
      .stream()
      .map(Category::getNameCategory)
      .toList();

    dto.setCategories(categories);

    return dto;
  }

  @Override
  public BookDetailResponseDTO getById(Integer id) {
    Book book = bookRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Book not found"));

    return mapToDetailDTO(book);
  }

  @Override
  public Page<Book> searchBooks(BookFilter bookFilter) {
    Specification<Book> spec = null;

    // Filter category
    if (
      bookFilter.getCategoryIds() != null &&
      !bookFilter.getCategoryIds().isEmpty()
    ) {
      spec = Specification.where(
        BookSpecification.hasCategories(bookFilter.getCategoryIds())
      );
    }

    // Filter topic
    if (bookFilter.getTopics() != null && !bookFilter.getTopics().isEmpty()) {
      spec =
        spec == null
          ? Specification.where(
              BookSpecification.hasTopics(bookFilter.getTopics())
            )
          : spec.and(BookSpecification.hasTopics(bookFilter.getTopics()));
    }

    // Filter price
    if (bookFilter.getMinPrice() != null || bookFilter.getMaxPrice() != null) {
      spec =
        spec == null
          ? Specification.where(
              BookSpecification.priceBetween(
                bookFilter.getMinPrice(),
                bookFilter.getMaxPrice()
              )
            )
          : spec.and(
              BookSpecification.priceBetween(
                bookFilter.getMinPrice(),
                bookFilter.getMaxPrice()
              )
            );
    }

    // SORT
    Sort sort = Sort.unsorted();

    if ("price_asc".equalsIgnoreCase(bookFilter.getSort())) {
      sort = Sort.by("price").ascending();
    } else if ("price_desc".equalsIgnoreCase(bookFilter.getSort())) {
      sort = Sort.by("price").descending();
    } else if ("newest".equalsIgnoreCase(bookFilter.getSort())) {
      sort = Sort.by("bookId").descending();
    }

    Pageable pageable = PageRequest.of(
      bookFilter.getPage(),
      bookFilter.getSize(),
      sort
    );

    return bookRepository.findAll(spec, pageable);
  }

  @Override
  public List<BookHomeResponseDTO> getHomeBooks() {
    List<Book> books = bookRepository.findAll();

    return books
      .stream()
      .map(book -> {
        // ===== Lấy thumbnail =====
        String imageUrl = book
          .getBookImages()
          .stream()
          .sorted((a, b) ->
            Boolean.compare(
              Boolean.TRUE.equals(b.getIsThumbNail()),
              Boolean.TRUE.equals(a.getIsThumbNail())
            )
          )
          .map(BookImage::getImageUrl)
          .findFirst()
          .orElse(null);

        // ===== Lấy danh sách category =====
        List<String> categories = book
          .getCategories()
          .stream()
          .map(Category::getNameCategory)
          .collect(Collectors.toList());

        return new BookHomeResponseDTO(
          book.getBookId(), // id
          book.getTitle(), // name
          categories, // categories
          imageUrl, // image
          book.getDiscountPrice(), // price
          book.getPrice(), // originalPrice
          book.getDescription() // description
        );
      })
      .collect(Collectors.toList());
  }

  @Override
  public void exportBooksToExcel(HttpServletResponse response)
    throws IOException {
    List<Book> books = bookRepository.findAllWithRelations();

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("BOOK LIST");

    // ================= HEADER STYLE =================
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);

    CellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFont(headerFont);
    headerStyle.setAlignment(HorizontalAlignment.CENTER);

    // ================= MONEY STYLE =================
    CellStyle moneyStyle = workbook.createCellStyle();
    DataFormat format = workbook.createDataFormat();
    moneyStyle.setDataFormat(format.getFormat("#,##0 \"VNĐ\""));

    // ================= CREATE HEADER =================
    Row headerRow = sheet.createRow(0);

    String[] columns = {
      "ID",
      "Title",
      "Price",
      "Discount Price",
      "Stock",
      "Categories",
    };

    for (int i = 0; i < columns.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(columns[i]);
      cell.setCellStyle(headerStyle);
    }

    Drawing<?> drawing = sheet.createDrawingPatriarch();

    int rowNum = 1;

    // ================= WRITE DATA =================
    for (Book book : books) {
      Row row = sheet.createRow(rowNum++);

      // ID
      row
        .createCell(0)
        .setCellValue(book.getBookId() != null ? book.getBookId() : 0);

      // Title
      row
        .createCell(1)
        .setCellValue(book.getTitle() != null ? book.getTitle() : "");

      // Price
      Cell priceCell = row.createCell(2);
      BigDecimal price = book.getPrice();
      priceCell.setCellValue(price != null ? price.doubleValue() : 0);
      priceCell.setCellStyle(moneyStyle);

      // Discount
      Cell discountCell = row.createCell(3);
      BigDecimal discount = book.getDiscountPrice();
      discountCell.setCellValue(discount != null ? discount.doubleValue() : 0);
      discountCell.setCellStyle(moneyStyle);

      // Stock
      row
        .createCell(4)
        .setCellValue(book.getStock() != null ? book.getStock() : 0);

      // Categories
      String categories = book
        .getCategories()
        .stream()
        .map(Category::getNameCategory)
        .collect(Collectors.joining(", "));
      row.createCell(5).setCellValue(categories);
    }

    // ================= AUTO SIZE =================
    for (int i = 0; i < columns.length - 1; i++) {
      sheet.autoSizeColumn(i);
    }

    workbook.write(response.getOutputStream());
    workbook.close();
  }
}
