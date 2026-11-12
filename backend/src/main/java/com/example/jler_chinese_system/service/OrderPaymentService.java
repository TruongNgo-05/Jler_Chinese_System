package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.entity.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderPaymentService {

  @PersistenceContext
  private EntityManager em;

  // Caller holds the order lock. Membership creation is serialized per user.
  public void complete(Order order, String transactionId) {
    if (order.getPaymentStatus() == Order.PaymentStatus.paid) return;
    em.lock(order.getUser(), LockModeType.PESSIMISTIC_WRITE);
    order.setPaymentStatus(Order.PaymentStatus.paid);
    order.setPaymentAt(LocalDateTime.now());
    order.setTransactionId(transactionId);
    for (var item : order.getOderItems())
      if (item.getItemType() == OrderItem.ItemType.Course) {
        boolean exists =
          em
            .createQuery(
              "select count(u) from UserCourse u where u.user.userId=:u and u.course.courseId=:c and u.status='ACTIVE'",
              Long.class
            )
            .setParameter("u", order.getUser().getUserId())
            .setParameter("c", item.getItemId())
            .getSingleResult() > 0;
        if (!exists) {
          var uc = new UserCourse();
          uc.setUser(order.getUser());
          uc.setCourse(em.getReference(Course.class, item.getItemId()));
          uc.setStatus("ACTIVE");
          em.persist(uc);
        }
      }
    if (!Boolean.TRUE.equals(order.getHasPhysicalItem())) order.setStatus(
      "COMPLETED"
    );
  }
}
