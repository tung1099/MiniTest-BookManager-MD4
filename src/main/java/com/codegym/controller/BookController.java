package com.codegym.controller;


import com.codegym.model.Book;
import com.codegym.model.BookForm;
import com.codegym.model.Category;
import com.codegym.service.book.IBookService;
import com.codegym.service.category.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@PropertySource("classpath:upload_file.properties")
public class BookController {

    @Autowired
    private IBookService bookService;

    @Autowired
    private ICategoryService categoryService;

    @Value("${file-upload}")
    private String fileUpload;

    @ModelAttribute("category")
    public Iterable<Category> categories(){
        return categoryService.findAll();
    }

    @GetMapping("/create-book")
    public ModelAndView showCreateBookForm(){
        ModelAndView modelAndView = new ModelAndView("book/create");
        modelAndView.addObject("book", new Book());
        return modelAndView;
    }
    @PostMapping("/create-book")
    public ModelAndView saveBook(@ModelAttribute("book") BookForm bookForm){
        //Lấy file ảnh
        MultipartFile file = bookForm.getImage();

        //Lấy tên file
        String fileName = file.getOriginalFilename();

        //Lấy thông tin Book
        String name = bookForm.getName();;
        int price = bookForm.getPrice();
        String author = bookForm.getAuthor();
        Category category = bookForm.getCategory();

        //Copy File
        try {
            FileCopyUtils.copy(file.getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Book book = new Book(name,price,author,fileName,category);

        bookService.save(book);
        ModelAndView modelAndView = new ModelAndView("book/create");
        modelAndView.addObject("book", new Book());
        modelAndView.addObject("message", "Success!!!");
        return modelAndView;
    }

    @GetMapping("/books")
    public ModelAndView listBook(@PageableDefault(value = 3) Pageable pageable,
                                 @RequestParam("search") Optional<String> keyword){

//        Iterable<Book> books = bookService.findAll();
        ModelAndView modelAndView = new ModelAndView("book/list");
        Page<Book> books ;

        if (keyword.isPresent()){
            books = bookService.findAllByNameContaining(keyword.get(), pageable);
        }else {
            books = bookService.findAll(pageable);
        }

        modelAndView.addObject("books", books);
        return modelAndView;
    }

    @GetMapping("/edit-book/{id}")
    public ModelAndView showEditBookForm(@PathVariable Long id){
        Optional<Book> book = bookService.findById(id);
        if (book.isPresent()){
            ModelAndView modelAndView = new ModelAndView("book/edit");
            modelAndView.addObject("book", book.get());
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("error.404");
            return modelAndView;
        }
    }
    @PostMapping("/edit-book")
    public ModelAndView updateBook(@ModelAttribute("book") Book book) {
        bookService.save(book);
        ModelAndView modelAndView = new ModelAndView("book/edit");
        modelAndView.addObject("book", book);
        modelAndView.addObject("message", "Success!!! ");
        return modelAndView;
    }

    @GetMapping("/delete-book/{id}")
    public ModelAndView showDeleteBookForm(@PathVariable Long id) {
        Optional<Book> book = bookService.findById(id);
        if (book.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("book/delete");
            modelAndView.addObject("book", book.get());
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("error.404");
            return modelAndView;
        }
    }

    @PostMapping("/delete-book")
    public String deleteCustomer(@ModelAttribute("book") Book book) {
        bookService.remove(book.getId());
        return "redirect:books";
    }


}
