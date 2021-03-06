package io.quarkus.qe.many.extensions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/hello")
public class ManyExtensionsResource {

    @GetMapping
    public String hello() {
        return "hello";
    }
}