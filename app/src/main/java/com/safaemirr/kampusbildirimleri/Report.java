package com.safaemirr.kampusbildirimleri;

/*
 * Bu sınıf uygulamada kullanılan bir bildirimi temsil eden
 * model sınıfıdır. Veritabanından gelen veriler bu sınıf üzerinden
 * activity ve adapter’lara taşınır.
 */
public class Report {

    // Bildirime ait temel bilgiler
    int id;
    String title, description, category, location, photo, status, user, date;

    // Bildirimin konum bilgileri
    double latitude, longitude;

    /*
     * Report nesnesi oluşturulurken tüm alanlar constructor
     * aracılığıyla atanır.
     */
    public Report(int id,
                  String title,
                  String description,
                  String category,
                  String location,
                  String photo,
                  String status,
                  String user,
                  String date,
                  double latitude,
                  double longitude) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.photo = photo;
        this.status = status;
        this.user = user;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Bildirimin ID bilgisi
    public int getId() {
        return id;
    }

    // Bildirim başlığı
    public String getTitle() {
        return title;
    }

    // Bildirim açıklaması
    public String getDescription() {
        return description;
    }

    // Bildirim kategorisi
    public String getCategory() {
        return category;
    }

    // Bildirim konum metni 
    public String getLocation() {
        return location;
    }

    // Bildirime eklenen fotoğrafın yolu
    public String getPhoto() {
        return photo;
    }

    // Bildirimin durumu
    public String getStatus() {
        return status;
    }

    // Bildirimi oluşturan kullanıcı bilgisi
    public String getUser() {
        return user;
    }

    // Bildirimin oluşturulma tarihi
    public String getDate() {
        return date;
    }

    // Bildirimin enlem bilgisi
    public double getLatitude() {
        return latitude;
    }

    // Bildirimin boylam bilgisi
    public double getLongitude() {
        return longitude;
    }

    /*
     * Bazı activity ve adapter’larda daha anlamlı bir isimlendirme
     * kullanmak için user alanını createdBy olarak döndürür.
     */
    public String getCreatedBy() {
        return user;
    }
}
