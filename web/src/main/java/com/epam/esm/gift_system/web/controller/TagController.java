package com.epam.esm.gift_system.web.controller;

import com.epam.esm.gift_system.service.TagService;
import com.epam.esm.gift_system.service.dto.TagDto;
import com.epam.esm.gift_system.web.hateaos.HateaosBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;
    private final HateaosBuilder hateaosBuilder;

    @Autowired
    public TagController(TagService tagService, HateaosBuilder hateaosBuilder) {
        this.tagService = tagService;
        this.hateaosBuilder = hateaosBuilder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('tags:create')")
    public TagDto create(@RequestBody TagDto tagDto) {
        TagDto created = tagService.create(tagDto);
        hateaosBuilder.setLinks(created);
        return created;
    }

    @GetMapping("/{id}")
    public TagDto findById(@PathVariable Long id) {
        TagDto tagDto = tagService.findById(id);
        hateaosBuilder.setLinks(tagDto);
        return tagDto;
    }

    @GetMapping
    public Page<TagDto> findAll(Pageable pageable) {
        Page<TagDto> page = tagService.findAll(pageable);
        page.getContent().forEach(hateaosBuilder::setLinks);
        return page;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('tags:delete')")
    public void delete(@PathVariable Long id) {
        tagService.delete(id);
    }
}