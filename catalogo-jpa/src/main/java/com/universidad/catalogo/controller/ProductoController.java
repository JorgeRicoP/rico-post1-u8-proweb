package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.CategoriaService;
import com.universidad.catalogo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/templates/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("templates/productos", productoService.listarTodos());
        return "templates/productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "templates/productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Producto producto,
                          BindingResult result,
                          @RequestParam(required = false) Long categoriaId,
                          Model model) {
        if (categoriaId == null) {
            result.rejectValue("categoria", "obligatoria", "Debe seleccionar una categoría");
        }
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("categoriaId", categoriaId);
            return "templates/productos/formulario";
        }
        productoService.guardar(producto, categoriaId);
        return "redirect:/productos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }

    // Endpoint que usa la consulta JPQL personalizada
    @GetMapping("/categoria/{categoriaId}/precio-mayor")
    public String porCategoriaConPrecioMayor(@PathVariable Long categoriaId,
                                             @RequestParam BigDecimal minimo,
                                             Model model) {
        model.addAttribute("templates/productos",
                productoService.listarPorCategoriaConPrecioMayorA(categoriaId, minimo));
        model.addAttribute("categoria", categoriaService.buscarPorId(categoriaId));
        model.addAttribute("minimo", minimo);
        return "templates/productos/filtrados";
    }
}