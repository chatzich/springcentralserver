package com.centralserver.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "numberarray")
public class NumberArray extends AuditModel {
    @Id
    @GeneratedValue(generator = "numberarray_generator")
    @SequenceGenerator(
            name = "numberarray_generator",
            sequenceName = "numberarray_sequence",
            initialValue = 1000
    )
    private Long id;

    
    @NotBlank
    @Column(columnDefinition = "text")
    private String original;
    
    @Column(columnDefinition = "text")
    private String sorted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
   
     public String getSorted() {
        return sorted;
    }

    public void setSorted(String sorted) {
        this.sorted = sorted;
    }
    
    
    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
