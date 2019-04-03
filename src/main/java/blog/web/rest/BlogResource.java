
package blog.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.domain.Blog;
import blog.network.ChaincodeResource;
import blog.repository.BlogRepository;
import blog.web.rest.errors.BadRequestAlertException;
import blog.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Blog.
 */
@RestController
@RequestMapping("/api")
public class BlogResource extends ChaincodeResource {

	private final Logger log = LoggerFactory.getLogger(BlogResource.class);

	private static final String ENTITY_NAME = "blog";

	private final BlogRepository blogRepository;

	public BlogResource(BlogRepository blogRepository) {
		this.blogRepository = blogRepository;
	}

	/**
	 * POST /blogs : Create a new blog.
	 *
	 * @param blog
	 *            the blog to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         blog, or with status 400 (Bad Request) if the blog has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PostMapping("/blogs")
	public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) throws URISyntaxException {
		log.debug("REST request to save Blog : {}", blog);
		if (blog.getId() != null) {
			throw new BadRequestAlertException("A new blog cannot already have an ID", ENTITY_NAME, "idexists");
		}

		Blog result = blogRepository.save(blog);

		// Process blockchain add request
		log.debug("BLOCKCHAIN ADD: " + blog.getId().toString() + " with the value: " + blog.toString());
		
		// @TODO Should use a BlogDTO from the BlogMapper (if DTO)
		ResponseEntity<String> response = super.addRequest(blog.getId().toString(), blog.toString());

		log.debug("BLOCKCHAIN ADD RESPONSE: " + response);

		return ResponseEntity.created(new URI("/api/blogs/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
	}

	/**
	 * PUT /blogs : Updates an existing blog.
	 *
	 * @param blog
	 *            the blog to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         blog, or with status 400 (Bad Request) if the blog is not valid, or
	 *         with status 500 (Internal Server Error) if the blog couldn't be
	 *         updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PutMapping("/blogs")
	public ResponseEntity<Blog> updateBlog(@RequestBody Blog blog) throws URISyntaxException {
		log.debug("REST request to update Blog : {}", blog);
		if (blog.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}

		// Process blockchain set request
		log.debug("BLOCKCHAIN UPDATE: " + blog.getId().toString() + " to the value: " + blog.toString());
		
		// @TODO Should use a BlogDTO from the BlogMapper (if DTO)
		ResponseEntity<String> response = super.setRequest(blog.getId().toString(), blog.toString());
		log.debug("BLOCKCHAIN UPDATE RESPONSE: " + response);

		Blog result = blogRepository.save(blog);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, blog.getId().toString()))
				.body(result);
	}

	/**
	 * GET /blogs : get all the blogs.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of blogs in body
	 */
	@GetMapping("/blogs")
	public List<Blog> getAllBlogs() {
		log.debug("REST request to get all Blogs");
		return blogRepository.findAll();
	}

	/**
	 * GET /blogs/:id : get the "id" blog.
	 *
	 * @param id
	 *            the id of the blog to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the blog, or
	 *         with status 404 (Not Found)
	 */
	@GetMapping("/blogs/{id}")
	public ResponseEntity<Blog> getRequest(@PathVariable Long id) {
		log.debug("REST request to get Blog : {}", id);
		
		// @TODO Should find the blog into the chain
		Optional<Blog> blog = blogRepository.findById(id);
		return ResponseUtil.wrapOrNotFound(blog);
	}

	/**
	 * DELETE /blogs/:id : delete the "id" blog.
	 *
	 * @param id
	 *            the id of the blog to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/blogs/{id}")
	public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
		log.debug("REST request to delete Blog : {}", id);

		// Process blockchain delete request
		log.debug("BLOCKCHAIN DELETE: " + id.toString());
		ResponseEntity<String> response = super.deleteRequest(id.toString());
		log.debug("BLOCKCHAIN DELETE RESPONSE: " + response);

		blogRepository.deleteById(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
	}

	// CHAINCODE

//	/**
//	 * GET /blogs/{entity}/chaincode : Get an entity value from the blockchain
//	 *
//	 * @param entity
//	 *            the entity to query
//	 * @return the ResponseEntity with status 200 (OK) and the value of the entity,
//	 *         or with status 417 (EXPECTATION_FAILED), or with status 500
//	 *         (INTERNAL_SERVER_ERROR)
//	 */
//	@GetMapping("/blogs/{entity}/chaincode")
//	public ResponseEntity<String> getRequest(@PathVariable String entity) {
//		return super.getRequest(entity);
//	}
//
//	/**
//	 * PUT /blogs/{entity}/chaincode : add a new value to the blockchain.
//	 *
//	 * @param value
//	 *            the hash of the diploma we want to add to the BC
//	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
//	 *         with status 417 (EXPECTATION_FAILED), or with status 500
//	 *         (INTERNAL_SERVER_ERROR)
//	 */
//	@PutMapping("/blogs/{entity}/chaincode")
//	public ResponseEntity<String> addRequest(@PathVariable String entity, String value) {
//		return super.addRequest(entity, value);
//	}
//
//	/**
//	 * POST /blogs/{entity}/chaincode : set an entity in the blockchain.
//	 *
//	 *
//	 * @param entity
//	 *            the entity to add to the blockchain
//	 * @param value
//	 *            the value to set the entity to
//	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
//	 *         with status 417 (EXPECTATION_FAILED), or with status 500
//	 *         (INTERNAL_SERVER_ERROR)
//	 */
//	@PostMapping("/blogs/{entity}/chaincode")
//	public ResponseEntity<String> setRequest(@PathVariable String entity, String value) {
//		return super.setRequest(entity, value);
//	}
//
//	/**
//	 * DELETE /blogs/{entity}/chaincode : delete an entity from the blockchain.
//	 *
//	 * @param entity
//	 *            to delete from the blockchain
//	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
//	 *         with status 417 (EXPECTATION_FAILED), or with status 500
//	 *         (INTERNAL_SERVER_ERROR)
//	 */
//	@DeleteMapping("/blogs/{entity}/chaincode")
//	public ResponseEntity<String> deleteRequest(@PathVariable String entity) {
//		return super.deleteRequest(entity);
//	}

}
