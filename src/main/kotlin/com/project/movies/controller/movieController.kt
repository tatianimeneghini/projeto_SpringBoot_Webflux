package com.project.movies.controller

import com.project.movies.exception.NotFoundException
import com.project.movies.model.Movie
import com.project.movies.repository.MovieRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

// A anotação @Autowired indica que faremos uma injeção de dependência, injetando no repositório
//      para poder persistir e buscar os filmes cadastrados.

// O retorno do método de criação é Mono, ou seja, stream que retornará 0 ou 1.

// O retorno do método de consulta é Flux, stream que retornará 0 ou N.

// No método de excluir um filme, utiliza-se o `flatMap` desempacota um stream e reempacota novamente
//      para o mesmo tipo ou outro (o `map` não faz isso). O método `then` ignora o retorno do `map` e
//      volta um Mono.empty(), que nada mais é que uma stream vazia.
// Outro detalhe: o retorno do tipo Mono<Void> é uma stream vazia, poisa resposta é no content (204)
@RestController
@RequestMapping(value = ["/movie"])
class movieController @Autowired constructor(
    private val repository: MovieRepository
){

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createMovie(@RequestBody movie: Movie): Mono<Movie> {
        movie.id = UUID.randomUUID().toString()

        return repository.save(movie)
    }

    @GetMapping(produces = ["application/stream+json"])
    fun get(): Flux<Movie> {
        return repository.findAll()
            .delayElements(Duration.ofSeconds(3))
    }

    @PutMapping
    fun put(@RequestBody movie: Movie): Mono<Movie> {
        return repository.save(movie)
    }

    @DeleteMapping(value = ["/{id}"])
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Void> {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException))
            .flatMap { movie -> repository.delete(movie) }
            .then(Mono.empty())
    }

}