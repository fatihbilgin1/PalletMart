# PalletMart

 PALLETMART                 


Özet

Bu proje, alıcı ve satıcıları ortak bir platformda bir araya getirerek, palet alışverişini dijital ortamda daha hızlı, pratik ve etkili bir şekilde gerçekleştirmelerini sağlamak amacıyla geliştirilmiştir. Kullanıcıların hem iletişim kurabileceği hem de ürünlere kolayca erişim sağlayabileceği bir mobil uygulama tasarlanmıştır. Uygulama, kullanıcı dostu bir ara yüz sunarak, iş süreçlerini kolaylaştırmayı ve sektörel ihtiyaçlara modern bir çözüm sunmayı hedeflemektedir.

Giriş

Günümüz dünyasında, dijitalleşme iş süreçlerinin her alanına nüfuz etmiştir. İnsanlar artık pek çok işlemi çevrim içi platformlar üzerinden gerçekleştirmeyi tercih etmektedir. PalletMart, palet ticareti yapan alıcı ve satıcıların, fiziksel bir buluşma ihtiyacı olmadan ürünlere erişmesini, sipariş vermesini ve iletişim kurmasını sağlayan bir çözüm sunmaktadır.
Bu projenin hedef kitlesi, palet alımı ve satımı yapan fabrikalar, işletmeler ve bireysel kullanıcıları içermektedir. Temel amaç, bu kişilerin ihtiyaçlarını en hızlı ve kolay şekilde karşılamaktır.


Yöntem

Teknolojik Altyapı:
PalletMart, Kotlin programlama dili kullanılarak geliştirilmiş, verilerin güvenli ve hızlı bir şekilde saklanmasını sağlamak için Firebase Realtime Database teknolojisinden faydalanılmıştır.
Veri Yapısı:
Veritabanında kullanılan düğümler:

•	Users Düğümü: Kullanıcı bilgilerini saklar.

•	Products Düğümü: Satışa sunulan ürünlerin detaylarını içerir.

•	Orders Düğümü: Sipariş bilgilerini yönetir.

•	Cart Düğümü: Kullanıcıların sepet işlemlerini takip eder.


Uygulama Akışı:

Akış şeması incelendiğinde, uygulamanın işleyişi şu şekilde özetlenebilir:
1.	Kullanıcı Girişi:
Uygulama çalıştırıldığında, kullanıcı mevcut bir hesabı yoksa "Kayıt Ol" ekranına yönlendirilir. Kayıt işlemi tamamlandıktan sonra kullanıcı olarak giriş yapılır.
2.	Alıcı İşlemleri:
Kullanıcı, ana ekranda ürün listesi ile karşılanır. İlgilendiği ürünlerin detaylarını inceleyebilir, sepetine ekleyebilir ve sipariş verebilir.
3.	Satıcı İşlemleri:
Kullanıcı "Satıcı Ol" butonunu kullanarak satıcı rolüne geçebilir. Satıcı olarak ürün ekleme, güncelleme ve silme işlemleri gerçekleştirebilir.
4.	Admin Paneli:
Yönetici, uygulamada tüm kullanıcıları ve ürünleri görüntüleyebilir, düzenleyebilir veya silebilir. Ayrıca yeni kullanıcılar ekleyebilir, siparişleri onaylayarak kargo sürecini başlatabilir ve kendi bilgilerini yönetebilir.



                                                                                                                                      

Deneysel Sonuçlar

Uygulama, geliştirilen tüm ekranlarda detaylı bir şekilde test edilmiştir. Firebase Realtime Database ile yapılan işlemler hatasız bir şekilde çalışmaktadır. Çeşitli kullanıcı rolleri (alıcı, satıcı ve admin) tanımlanarak sistemin çoklu senaryolarda performansı doğrulanmıştır.
Kullanıcı deneyimi açısından, uygulamanın tasarımı açık ve anlaşılır bir şekilde oluşturulmuştur. Kullanıcıların kolayca adapte olabileceği bir arayüz sunulmuştur.
Proje kapsamında belirlediğim hedeflerin büyük bir kısmını gerçekleştirmiş olsam da, admin/satıcı panellerine daha fazla işlevsellik kazandırmak gibi geliştirmeler ileride yapılacaklar arasında yer almaktadır.
________________________________________
Sonuç

PalletMart projesi, palet alım-satım sürecini dijitalleştirerek sektörel bir ihtiyaca yanıt vermeyi başarmıştır. Kullanıcı dostu yapısı ve etkili işlevselliği ile hedeflere büyük oranda ulaşılmıştır. Gelecekte, uygulamanın daha fazla özelliğe sahip hale getirilmesi için planlamalar yapılmıştır.
________________________________________
Kaynakça

•	Google Firebase Realtime Database https://firebase.google.com/products/realtime-database
•	Kotlin Resmi Dokümantasyonu https://kotlinlang.org/docs/home.html
•	Android Developers: Uygulama Geliştirme https://developer.android.com/


![Login](https://github.com/user-attachments/assets/bb90573b-5a68-4081-a0cc-010c32bf0459)
![Register](https://github.com/user-attachments/assets/2e389d7d-efb3-4e35-bcc8-d2d7d8abb2c2)
![Admin Ana Ekran](https://github.com/user-attachments/assets/17cd71b5-35ff-404c-a16e-b347356fd4fb)
![Admin Ürün Yönetim](https://github.com/user-attachments/assets/fa815214-49e3-45b1-a7d0-8c252389b42d)
![Admin Ürün Detay](https://github.com/user-attachments/assets/6df2dee1-0639-4869-a5a6-1082bcfec16b)
![AddProduct](https://github.com/user-attachments/assets/99c97db4-a84a-4e57-b5a4-929f75bc6dfc)
![Admin Kullanıcı Yönetim](https://github.com/user-attachments/assets/865c40db-7de2-4529-abdd-e21be1ac399c)
![AddUsers](https://github.com/user-attachments/assets/9f425a5f-01e7-4e5e-9613-36af62fb0460)
![Admin Kullanıcı Detay](https://github.com/user-attachments/assets/5dfb43b4-a9af-4b63-97fe-29689ea5d916)
![Admin Sipariş Yönetim](https://github.com/user-attachments/assets/43752bb8-2c5d-4a21-b38d-0be86dc76877)
![Admin Sipariş Detay](https://github.com/user-attachments/assets/73b2cb04-4cb1-4906-9d42-0bc62b6ca1a0)
![Admin Settings](https://github.com/user-attachments/assets/c187d1cf-6f9e-421a-a29d-2106f55bf807)
![Kullanıcı Ana Ekran](https://github.com/user-attachments/assets/b099359b-cfd1-491a-ab1c-ea6444a38c76)
![Kullanıcı Ürün Detay](https://github.com/user-attachments/assets/c88b24bd-93c6-42ad-975f-e4c439bbb19f)
![Kullanıcı Sepet](https://github.com/user-attachments/assets/7b574dc8-0e1c-480a-85ce-3b1d4bc743ad)
![Ödeme](https://github.com/user-attachments/assets/24a89bea-b61c-43a4-ac8d-5d78a3f4b441)
![Kullanıcı İnformation](https://github.com/user-attachments/assets/f743793d-f8b7-49c9-a0bd-5098401eb10b)
![Satıcı Ana Ekran](https://github.com/user-attachments/assets/346fbd65-019c-44c1-9acb-0f66491955ed)
![Satıcı Ürün Detay](https://github.com/user-attachments/assets/f5b0d15c-bb39-4c4d-82ec-230ffb3b3a63)
![Satıcı Settings](https://github.com/user-attachments/assets/019bef11-7083-4f0a-9fc7-8f491ca5eaea)


