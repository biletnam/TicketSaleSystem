package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Category;
import ru.tersoft.service.CategoryService;
import ru.tersoft.utils.ResponseFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/attractions/cat")
@Api(description = "Work with attraction categories", tags = {"Category"})
public class CategoryController {
    @Resource(name="CategoryService")
    private CategoryService categoryService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of categories")
    public List<Category> getCategories() {
        return (List<Category>)categoryService.getAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Add new category", notes = "Admin access required", response = Category.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> add(@RequestBody Category category) {
        if(category != null)
            return ResponseFactory.createResponse(categoryService.add(category));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty category");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit category", notes = "Admin access required", response = Category.class)
    public ResponseEntity<?> edit(@RequestBody Category category) {
        if(category != null) {
            Boolean isEdited = categoryService.edit(category);
            if(isEdited)
                return ResponseFactory.createResponse(categoryService.get(category.getId()));
            else
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty category");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete category", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Boolean isDeleted = categoryService.delete(id);
        if(isDeleted)
            return ResponseFactory.createResponse();
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
    }
}
