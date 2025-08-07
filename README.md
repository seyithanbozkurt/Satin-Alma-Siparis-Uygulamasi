# Staj Projesi - Satın Alma Sipariş Uygulaması

Bu proje, bayiler ile merkez arasında ürün siparişi ve temin etkileşimini sağlayan microservice mimarisinde geliştirilmiş bir uygulamadır.

## Proje Yapısı

Proje 3 ana microservice'den oluşmaktadır:

### 1. Product Service (Port: 8081)
- Ürün oluşturma, güncelleme, silme, arama API'ları
- Stok yönetimi
- Ürün fiyatlandırma

### 2. Customer Service (Port: 8082)
- Müşteri oluşturma, güncelleme, silme, arama API'ları
- Sipariş yetkisi yönetimi
- Müşteri bilgileri yönetimi

### 3. Order Service (Port: 8083)
- Sipariş oluşturma, güncelleme, arama API'ları
- Müşteri sipariş yetkisi kontrolü
- Sipariş durumu takibi

## Teknolojiler

- **Backend**: Spring Boot 3.5.4
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA
- **API Documentation**: Swagger/OpenAPI
- **Containerization**: Docker & Docker Compose
- **Java Version**: 21

## Kurulum ve Çalıştırma

### Gereksinimler
- Docker ve Docker Compose
- Java 21 (geliştirme için)
- Maven (geliştirme için)

### Docker ile Çalıştırma (Önerilen)

1. Projeyi klonlayın:
```bash
git clone <repository-url>
cd StajProje
```

2. Docker Compose ile tüm servisleri başlatın:
```bash
docker-compose up -d
```

3. Servislerin başlamasını bekleyin (yaklaşık 2-3 dakika)

4. API dokümantasyonlarına erişin:
   - Product Service: http://localhost:8081/swagger-ui.html
   - Customer Service: http://localhost:8082/swagger-ui.html
   - Order Service: http://localhost:8083/swagger-ui.html

### Manuel Çalıştırma

1. MySQL veritabanını başlatın
2. Her servisi ayrı ayrı çalıştırın:
```bash
# Product Service
cd Product-service
./mvnw spring-boot:run

# Customer Service (yeni terminal)
cd Customer-service
./mvnw spring-boot:run

# Order Service (yeni terminal)
cd Order-service
./mvnw spring-boot:run
```

## API Endpoints

### Product Service (8081)
- `GET /api/products` - Tüm ürünleri listele
- `GET /api/products/{id}` - ID'ye göre ürün getir
- `POST /api/products` - Yeni ürün oluştur
- `PUT /api/products/{id}` - Ürün güncelle
- `DELETE /api/products/{id}` - Ürün sil
- `GET /api/products/search?name={name}` - İsme göre ürün ara
- `GET /api/products/search/price?minPrice={min}&maxPrice={max}` - Fiyat aralığına göre ara
- `GET /api/products/in-stock?minQuantity={qty}` - Stokta olan ürünleri getir
- `PATCH /api/products/{id}/stock?quantity={qty}` - Stok güncelle

### Customer Service (8082)
- `GET /api/customers` - Tüm müşterileri listele
- `GET /api/customers/{id}` - ID'ye göre müşteri getir
- `POST /api/customers` - Yeni müşteri oluştur
- `PUT /api/customers/{id}` - Müşteri güncelle
- `DELETE /api/customers/{id}` - Müşteri sil
- `GET /api/customers/with-order-permission` - Sipariş yetkisi olan müşterileri getir
- `PATCH /api/customers/{id}/grant-order-permission` - Sipariş yetkisi ver
- `PATCH /api/customers/{id}/revoke-order-permission` - Sipariş yetkisini kaldır
- `GET /api/customers/search?name={name}` - İsme göre müşteri ara
- `GET /api/customers/{id}/has-order-permission` - Sipariş yetkisi kontrolü

### Order Service (8083)
- `GET /api/orders` - Tüm siparişleri listele
- `GET /api/orders/{id}` - ID'ye göre sipariş getir
- `POST /api/orders` - Yeni sipariş oluştur
- `GET /api/orders/customer/{customerId}` - Müşteriye ait siparişleri getir
- `GET /api/orders/status/{status}` - Duruma göre siparişleri getir
- `GET /api/orders/number/{orderNumber}` - Sipariş numarasına göre getir
- `PATCH /api/orders/{id}/status?status={status}` - Sipariş durumunu güncelle

## Veritabanı Şeması

### Product Service Database (Productdb)
- `products` tablosu: Ürün bilgileri

### Customer Service Database (customerdb)
- `customers` tablosu: Müşteri bilgileri

### Order Service Database (orderdb)
- `orders` tablosu: Sipariş bilgileri
- `order_items` tablosu: Sipariş kalemleri

## Örnek Kullanım

### 1. Ürün Oluşturma
```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "Güçlü işlemci",
    "price": 15000.00,
    "stockQuantity": 10
  }'
```

### 2. Müşteri Oluşturma
```bash
curl -X POST http://localhost:8082/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmet Yılmaz",
    "email": "ahmet@example.com",
    "phone": "05551234567",
    "address": "İstanbul, Türkiye"
  }'
```

### 3. Sipariş Yetkisi Verme
```bash
curl -X PATCH http://localhost:8082/api/customers/1/grant-order-permission
```

### 4. Sipariş Oluşturma
```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
```

## Sipariş Durumları

- `PENDING`: Beklemede
- `CONFIRMED`: Onaylandı
- `SHIPPED`: Kargoda
- `DELIVERED`: Teslim edildi
- `CANCELLED`: İptal edildi

## Geliştirme Notları

- Tüm servisler JSON formatında veri alışverişi yapar
- Swagger UI ile API dokümantasyonuna erişilebilir
- Müşteri sipariş yetkisi yoksa sipariş oluşturulamaz
- Stok kontrolü otomatik olarak yapılır
- Sipariş numaraları otomatik olarak oluşturulur

## Lisans

Bu proje eğitim amaçlı geliştirilmiştir. 
