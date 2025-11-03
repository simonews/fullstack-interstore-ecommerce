package com.interstore.interstore_backend.model;

public enum StatoOrdine {
     IN_CORSO,
    ANNULLATO,
    FINALIZZATO;


    @Override
    public String toString() {
         return name();
    }
}
