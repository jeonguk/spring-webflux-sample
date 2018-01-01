package com.jeonguk.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.jeonguk.domain.Post;

@Repository
public interface PostRepository extends ReactiveMongoRepository<Post, String> {

}