package tn.zeros.smg.controllers.DTO;

import lombok.*;


public interface ArticleDTO {
    Long getId();
    String getDesignation();
    String getFrn();
    String getPAHT();
    int getSTOCK();
}
