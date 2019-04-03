
package blog.web.rest;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.network.ChaincodeResource;

/**
 * REST controller for managing Blog chaincode
 * (ONLY For Development).
 */
@RestController
@RequestMapping("/api")
@Profile("dev")
public class BlogChaincodeResource extends ChaincodeResource {

	private final BlogResource chaincode;

	public BlogChaincodeResource(BlogResource chaincode) {
		this.chaincode = chaincode;
	}

	/**
	 * GET /blogs/{entity}/chaincode : Get an entity value from the blockchain
	 *
	 * @param entity
	 *            the entity to query
	 * @return the ResponseEntity with status 200 (OK) and the value of the entity,
	 *         or with status 417 (EXPECTATION_FAILED), or with status 500
	 *         (INTERNAL_SERVER_ERROR)
	 */
	@GetMapping("/blogs/{entity}/chaincode")
	public ResponseEntity<String> getRequest(@PathVariable String entity) {
		return chaincode.getRequest(entity);
	}

	/**
	 * PUT /blogs/{entity}/chaincode : add a new value to the blockchain.
	 *
	 * @param value
	 *            the hash of the diploma we want to add to the BC
	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
	 *         with status 417 (EXPECTATION_FAILED), or with status 500
	 *         (INTERNAL_SERVER_ERROR)
	 */
	@PutMapping("/blogs/{entity}/chaincode")
	public ResponseEntity<String> addRequest(@PathVariable String entity, String value) {
		return chaincode.addRequest(entity, value);
	}

	/**
	 * POST /blogs/{entity}/chaincode : set an entity in the blockchain.
	 *
	 *
	 * @param entity
	 *            the entity to add to the blockchain
	 * @param value
	 *            the value to set the entity to
	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
	 *         with status 417 (EXPECTATION_FAILED), or with status 500
	 *         (INTERNAL_SERVER_ERROR)
	 */
	@PostMapping("/blogs/{entity}/chaincode")
	public ResponseEntity<String> setRequest(@PathVariable String entity, String value) {
		return chaincode.setRequest(entity, value);
	}

	/**
	 * DELETE /blogs/{entity}/chaincode : delete an entity from the blockchain.
	 *
	 * @param entity
	 *            to delete from the blockchain
	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
	 *         with status 417 (EXPECTATION_FAILED), or with status 500
	 *         (INTERNAL_SERVER_ERROR)
	 */
	@DeleteMapping("/blogs/{entity}/chaincode")
	public ResponseEntity<String> deleteRequest(@PathVariable String entity) {
		return chaincode.deleteRequest(entity);
	}

}
