package com.ecommerce.microcommerce.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

//@JsonIgnoreProperties(value = {"prixAchat", "id"}) Si on veut cacher plusieurs propriétés de la classe
//@JsonFilter("monFiltreDynamique") // Pour avoir un filtre dynamique sur ce qui est exposable
@Entity
public class Product {
    // informations exposables
    @Id
    private int id;
    @Size(min=3, max=25)
    private String nom;
    @Min(value=1)
    private int prix;

    // informations non exposables

    //@JsonIgnore // a mettre devant chaque propriété que l'on veut masquer
    private int prixAchat;

    public Product() {
    }

    public Product(int id, String nom, int prix, int prixAchat) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.prixAchat = prixAchat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(int prixAchat) {
        this.prixAchat = prixAchat;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                '}';
    }
}
