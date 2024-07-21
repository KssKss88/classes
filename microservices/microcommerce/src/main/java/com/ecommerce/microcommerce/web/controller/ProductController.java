package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.dao.ProductDao;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name="API pour les opérations CRUD sur les produits.")
@RestController
public class ProductController {
    @Autowired
    private final ProductDao productDao;

    public ProductController(ProductDao productDao){
        this.productDao = productDao;
    }
    @Operation(summary = "Récupère l'ensemble des produits en stock")
    @GetMapping("/Produits")
    public MappingJacksonValue listeProduits() {
        Iterable<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }

    @Operation(summary = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id){
        Product produit = productDao.findById(id);
        if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");
        return produit;
    }
    @Operation(summary = "Récupère tous les produits dont le prix est supérieur au prix cherché")
    @GetMapping(value = "test/produits/{prixLimit}")
    public List<Product> testDeRequetes(@PathVariable int prixLimit){
        return productDao.findByPrixGreaterThan(prixLimit);
    }
    //Equivalent :
    //@Query("SELECT id, nom, prix FROM Product p WHERE p.prix > :prixLimit")
    //List<Product>  chercherUnProduitCher(@Param("prixLimit") int prix);

    @Operation(summary = "Ajout d'un produit dans le stock")
    @PostMapping(value ="/Produits")
    public ResponseEntity<Product> ajouterProduit(@Valid @RequestBody Product product) {
        Product productAdded = productDao.save(product);
        if (Objects.isNull(productAdded)) {
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Supprime un produit grâce à son ID à condition que celui-ci soit en stock!")
    @DeleteMapping(value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id){
        productDao.deleteById(id);
    }

    @Operation(summary = "Modifie un produit grâce à son ID à condition que celui-ci soit en stock!")
    @PutMapping(value = "/Produits")
    public void updateProduit(@RequestBody Product product){
        productDao.save(product);
    }

    @Operation(summary = "Renvoie la marge de chaque produit en stock!")
    @GetMapping(value = "/AdminProduits")
    public MappingJacksonValue calculerMargeProduit() {
        Iterable<Product> produits = productDao.findAll();
        Map<Product, Integer> produitsAvecMarge = new HashMap<>();

        for (Product produit : produits){
            int marge = produit.getPrix() - produit.getPrixAchat();
            produitsAvecMarge.put(produit,marge);
        }
        //Filtrage de prixAchat dans les produits
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        //Filtrage de prixAchat dans la Hashmap
        SimpleBeanPropertyFilter mapFiltre = SimpleBeanPropertyFilter.serializeAllExcept("key.prixAchat");
        FilterProvider listeDeFiltresMap = new SimpleFilterProvider().addFilter("monFiltreDynamiqueMap", mapFiltre);

        // Combiner les filtres
        FilterProvider listDeNosFiltres = new SimpleFilterProvider()
                .addFilter("monFiltreDynamique", monFiltre)
                .addFilter("monFiltreDynamiqueMap", mapFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produitsAvecMarge);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }

}
