package com.my.blog.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class StaticResourceController {
    
    @GetMapping("/static/{type}/{file}")
    public ResponseEntity<Resource> serveStaticResource(
            @PathVariable String type,
            @PathVariable String file) throws IOException {
        
        String path = "static/" + type + "/" + file;
        Resource resource = new ClassPathResource(path);
        
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        MediaType mediaType = MediaType.APPLICATION_JSON;
        
        switch (type) {
            case "css":
                mediaType = MediaType.parseMediaType("text/css");
                break;
            case "js":
                mediaType = MediaType.parseMediaType("application/javascript");
                break;
            case "img":
                if (file.endsWith(".png")) {
                    mediaType = MediaType.IMAGE_PNG;
                } else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
                    mediaType = MediaType.IMAGE_JPEG;
                } else if (file.endsWith(".gif")) {
                    mediaType = MediaType.IMAGE_GIF;
                } else if (file.endsWith(".ico")) {
                    mediaType = MediaType.parseMediaType("image/x-icon");
                }
                break;
            case "fonts":
                if (file.endsWith(".ttf")) {
                    mediaType = MediaType.parseMediaType("font/ttf");
                } else if (file.endsWith(".woff")) {
                    mediaType = MediaType.parseMediaType("font/woff");
                } else if (file.endsWith(".woff2")) {
                    mediaType = MediaType.parseMediaType("font/woff2");
                }
                break;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}