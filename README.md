# Rankup Plugin

LuckPerms ve Vault Altyapısını Kullanan, **onaylı ve Konfigürasyon Tabanlı** Bir `/rankup` Sistemi.

Oyuncular Mevcut Rütbelerini Para Karşılığı Yükseltebilir, İşlem Onay Sistemiyle Yanlışlıkla Komut Kullanımın Önüne Geçilmiştir.
## ✨ Özellikler

- ✅ LuckPerms Grup Sistemiyle Tam Uyumlu
- 💰 Vault Destekli Ekonomi Entegrasyonu
- ⏳ Onay süresi
- 📢 Rank Atlama Duyurusu
- 🧩 Tamamen Config Üzerinden Yönetim
- 🔒 Maksimum Rütbe Desteği
- 🌍 Çoklu Grup Kontrolü (En Pahalı Rank Baz Alınır)
## 📦 Gereksinimler
Bu Eklentinin Çalışabilmesi İçin Aşağıdaki Eklentiler **zorunludur**:

- **Spigot / Paper** (1.16+ Önerilir)
- **Vault**
- **LuckPerms**
- Herhangi Bir Vault Uyumlu Ekonomi Eklentisi  
  (EssentialsX, CMI, vb.)
## ⚙️ Kurulum

1. `Rankup.jar` Dosyasını `plugins/` Klasörüne At
2. Sunucuyu Başlat veya Restart At
3. `config.yml` Otomatik Oluşacaktır
4. Rank Yapılandırmasını Düzenle
5. Sunucuyu Yeniden Başlat veya `/reload` (Pek Tavsiye Edilmez)
## 🧠 Çalışma Mantığı

- Oyuncunun LuckPerms Üzerindeki **Tüm Grupları** Kontrol Edilir
- Config’te Tanımlı Olanlar Arasından **En Yüksek Fiyatlı** Rank Baz Alınır
- Eğer Rank `last: true` İse Oyuncu Daha Fazla Rank Atlayamaz
- İlk `/rankup` Bilgi & Onay
- İkinci `/rankup` Para Çekilir ve Rank Verilir
