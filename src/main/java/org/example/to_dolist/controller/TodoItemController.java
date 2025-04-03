package org.example.to_dolist.controller; // Figyelj, hogy a csomagnév helyes legyen!

import org.example.to_dolist.model.TodoItem;
import org.example.to_dolist.service.TodoItemService;
import org.example.to_dolist.exception.ResourceNotFoundException; // Bár a controllerben már nem direkt dobjuk, itt maradhat importként, vagy törölhető
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/todos") // Minden endpoint /api/todos alatt lesz elérhető
public class TodoItemController {

    @Autowired // Spring automatikusan beinjektálja a TodoItemService egy példányát
    private TodoItemService todoItemService;

    /**
     * GET /api/todos
     * Lekéri az összes TodoItem-et.
     * @return Lista a TodoItem objektumokról.
     */
    @GetMapping
    public List<TodoItem> getAllTodos() {
        return todoItemService.getAllTodos();
    }

    /**
     * GET /api/todos/{id}
     * Lekér egy specifikus TodoItem-et az azonosítója alapján.
     * @param id A keresett TodoItem azonosítója.
     * @return ResponseEntity ami tartalmazza a megtalált TodoItem-et (200 OK) vagy 404 Not Found választ.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoItem> getTodoById(@PathVariable Long id) {
        // A Service Optional-t ad vissza, itt alakítjuk át ResponseEntity-vé
        return todoItemService.getTodoById(id)
                .map(todoItem -> ResponseEntity.ok(todoItem)) //ok() metódussal csomagoljuk
                .orElse(ResponseEntity.notFound().build()); // Ha az Optional üres, 404 Not Found választ építünk
    }

    /**
     * POST /api/todos
     * Létrehoz egy új TodoItem-et a kérés törzsében kapott adatok alapján.
     * @param todoItem A kérés törzséből (JSON) átalakított TodoItem objektum.
     * @return ResponseEntity ami tartalmazza a létrehozott TodoItem-et és 201 Created státuszkódot.
     */
    @PostMapping
    public ResponseEntity<TodoItem> createTodo(@RequestBody TodoItem todoItem) {
        // A @RequestBody annotáció jelzi, hogy a kérés törzséből kell kiolvasni az objektumot
        TodoItem createdTodo = todoItemService.createTodo(todoItem);
        // Visszatérünk a létrehozott erőforrással és a 201-es státusszal
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    /**
     * PUT /api/todos/{id}
     * Frissít egy létező TodoItem-et az azonosítója és a kérés törzsében kapott adatok alapján.
     * @param id A frissítendő TodoItem azonosítója.
     * @param todoDetails A kérés törzséből (JSON) átalakított TodoItem objektum a friss adatokkal.
     * @return ResponseEntity ami tartalmazza a frissített TodoItem-et
     * (200 OK) vagy 404 Not Found választ, ha az elem nem létezik.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoItem> updateTodo(@PathVariable Long id, @RequestBody TodoItem todoDetails) {
        try {
            TodoItem updatedTodo = todoItemService.updateTodo(id, todoDetails);
            return ResponseEntity.ok(updatedTodo); // 200 OK státusz a frissített elemmel
        } catch (ResourceNotFoundException e) {
            // Ha a service réteg ResourceNotFoundException-t dobott (mert nem találta az ID-t),
            // akkor 404 Not Found választ adunk.
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/todos/{id}
     * Töröl egy specifikus TodoItem-et az azonosítója alapján.
     * @param id A törlendő TodoItem azonosítója.
     * @return ResponseEntity 204 No Content státusszal sikeres
     * törlés esetén, vagy 404 Not Found választ, ha az elem nem létezik.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        try {
            todoItemService.deleteTodo(id);
            // Sikeres törlés esetén nincs szükség válasz törzsre, a 204-es státusz jelzi a sikert.
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            // Ha a service réteg ResourceNotFoundException-t dobott, 404 Not Found választ adunk.
            return ResponseEntity.notFound().build();
        }
    }
}