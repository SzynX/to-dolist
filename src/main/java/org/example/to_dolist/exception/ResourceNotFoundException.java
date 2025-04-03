package org.example.to_dolist.exception; // Ellenőrizd a csomagnevet!

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Ez az annotáció biztosítja, hogy ha ez a kivétel elkapásra kerül a Spring MVC által,
// akkor automatikusan 404 NOT FOUND HTTP választ küldjön vissza.
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}