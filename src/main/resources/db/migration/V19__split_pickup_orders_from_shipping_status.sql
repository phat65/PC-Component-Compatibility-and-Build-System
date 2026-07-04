UPDATE orders
SET status = 'Ready for Pickup'
WHERE status = 'Ready to Ship'
  AND (
      shipping_method = 'Pick up at Store'
      OR shipping_method LIKE '%Nhận tại cửa hàng%'
      OR shipping_method LIKE '%Nhan tai cua hang%'
  );
