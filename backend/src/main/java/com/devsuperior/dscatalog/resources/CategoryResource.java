package com.devsuperior.dscatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.entities.Category;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

	@GetMapping
	public ResponseEntity<List<Category>> findAll() {
		List<Category> categoriesList = new ArrayList<>();
		categoriesList.add(new Category(1L, "Books"));
		categoriesList.add(new Category(2L, "Phones"));
		categoriesList.add(new Category(3L, "Hardware"));
		return ResponseEntity.ok().body(categoriesList);
	}
}
