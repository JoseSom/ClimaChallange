package com.example.climachallange.mainModule.model

interface Mapper<D,E> {
    fun fromEntity(entity: E): D
    fun toEntity(domain:D): E?
}