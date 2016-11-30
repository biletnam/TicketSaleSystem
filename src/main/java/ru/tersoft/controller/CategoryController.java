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
import ru.tersoft.entity.ErrorResponse;
import ru.tersoft.service.CategoryService;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("attractions/cat")
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
        if(category != null) return new ResponseEntity<>(categoryService.add(category), HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Passed empty category"),
                        HttpStatus.BAD_REQUEST);
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
            if(isEdited) return new ResponseEntity<>(categoryService.get(category.getId()), HttpStatus.OK);
            else return new ResponseEntity<>
                    (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                            "Category with such id was not found"),
                            HttpStatus.NOT_FOUND);
        }
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Passed empty category"),
                        HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete category", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Boolean isDeleted = categoryService.delete(id);
        if(isDeleted) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Category with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }
}
