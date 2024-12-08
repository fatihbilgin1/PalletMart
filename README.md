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


