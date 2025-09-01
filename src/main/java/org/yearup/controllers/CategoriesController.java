package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
// http://localhost:8080/categories
@RequestMapping("/categories")
// add annotation to allow cross site origin requests
public class CategoriesController
{
    private final CategoryDao categoryDao;

    @Autowired
    private ProductDao productDao; // Add this field if not already present

    @Autowired
    public CategoriesController(CategoryDao categoryDao)
    {
        this.categoryDao = categoryDao;
    }

    // add the appropriate annotation for a get action
    @GetMapping
    public List<Category> getAll()
    {
        // find and return all categories
        return categoryDao.getAllCategories();
    }

    // add the appropriate annotation for a get action
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable int id)
    {
        Category category = categoryDao.getById(id);
        if (category == null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of products by categoryId
        return productDao.getByCategoryId(categoryId);
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category)
    {
        Category created = categoryDao.create(category);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        Category existing = categoryDao.getById(id);
        if (existing == null)
        {
            return ResponseEntity.notFound().build();
        }
        categoryDao.update(id, category);
        return ResponseEntity.noContent().build();
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id)
    {
        Category existing = categoryDao.getById(id);
        if (existing == null)
        {
            return ResponseEntity.notFound().build();
        }
        categoryDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
