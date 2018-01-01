package com.jeonguk;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.jeonguk.domain.Post;
import com.jeonguk.repository.PostRepository;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringWebfluxSampleApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    PostRepository postRepository;

    @Test
    public void testCreatePost() {
        Post post = new Post("This is a Test Post title", "Post cotent");

        webTestClient.post().uri("/posts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(post), Post.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo("This is a Test Post title")
                .jsonPath("$.content").isEqualTo("Post cotent");
    }
    
    @Test
    public void testGetAllPosts() {
        webTestClient.get().uri("/posts")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Post.class);
    }

    @Test
    public void testGetSinglePost() {
        Post post = postRepository.save(new Post("Hello, World!", "Hello, Content")).block();

        webTestClient.get()
                .uri("/posts/{id}", Collections.singletonMap("id", post.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response ->
                        Assertions.assertThat(response.getResponseBody()).isNotNull());
    }

    @Test
    public void testUpdatePost() {
        Post post = postRepository.save(new Post("Initial Post", "Initial Post Content")).block();

        Post newPostData = new Post("Updated Post", "Initial Post Content");

        webTestClient.put()
                .uri("/posts/{id}", Collections.singletonMap("id", post.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(newPostData), Post.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Updated Post");
    }

    @Test
    public void testDeletePost() {
        Post post = postRepository.save(new Post("To be deleted title", "To be deleted content")).block();

        webTestClient.delete()
                .uri("/posts/{id}", Collections.singletonMap("id",  post.getId()))
                .exchange()
                .expectStatus().isOk();
    }
    
}
