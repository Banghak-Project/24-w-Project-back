package org.project.exchange.model.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.project.exchange.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository{
    @PersistenceContext
    private EntityManager em;

    public void save(Product product){
        em.persist(product);
    }
    //상세,edit
    public Optional<Product> findById(String product_id){
        return Optional.ofNullable(em.find(Product.class, product_id));
    }
    //list 상관없이 모든 Product
    public List<Product> findAll(){
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }
    //list별 product
    public List<Product> findByListId(String listId){
        return em.createQuery("SELECT p FROM Product p WHERE p.list = :listId", Product.class)
                .setParameter("listId", listId)
                .getResultList();
    }
    //list 통째로 삭제
    public void deleteAllByListId(String listId){
        em.createQuery("DELETE FROM Product p WHERE p.list = :listId")
                .setParameter("listId", listId)
                .executeUpdate();
    }

    //특정 product 삭제
    public void delete(Product product){
        em.remove(product);
    }

    public void deleteByListId(String listId) {
        em.createQuery("DELETE FROM Product p WHERE p.list = :listId")
                .setParameter("listId", listId)
                .executeUpdate();
    }
}
