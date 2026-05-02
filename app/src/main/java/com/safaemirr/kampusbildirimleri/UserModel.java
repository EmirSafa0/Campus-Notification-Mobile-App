package com.safaemirr.kampusbildirimleri;

/*
 * UserModel
 * - Kullanıcıya ait temel bilgileri tutan model sınıfı
 * - DatabaseHelper üzerinden okunup Activity’lerde kullanılır
 */
public class UserModel {

    // Kullanıcıya ait alanlar
    int id;
    String name;
    String email;
    String role;
    String unit; // Kullanıcının bağlı olduğu birim / departman

    /*
     * Eski constructor
     * - unit alanı eklenmeden önceki yapıyla uyumluluk için korunur
     * - unit boş string olarak atanır
     */
    public UserModel(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.unit = "";
    }

    /*
     * Yeni constructor
     * - Profil ekranında birim bilgisini göstermek için kullanılır
     * - Database’den unit alanı okunarak doldurulur
     */
    public UserModel(int id, String name, String email, String role, String unit) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.unit = unit;
    }

    // Kullanıcı ID
    public int getId() {
        return id;
    }

    // Kullanıcı adı
    public String getName() {
        return name;
    }

    // Kullanıcı email adresi
    public String getEmail() {
        return email;
    }

    // Kullanıcı rolü (ADMIN / USER)
    public String getRole() {
        return role;
    }

    // Kullanıcının bağlı olduğu birim
    public String getUnit() {
        return unit;
    }
}
