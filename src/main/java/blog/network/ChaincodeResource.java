
package blog.network;

import javax.json.Json;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import blog.network.networkException.A_BlockchainException;
import blog.network.networkException.EntityNotFound;
import blog.network.request.Add;
import blog.network.request.Delete;
import blog.network.request.Get;
import blog.network.request.Set;

/**
 * Generic Chaincode Resource
 * @author Didier Donsez
 * @TODO should be a generic for value (ou a JSON doc)
 */
public class ChaincodeResource {

	private final Logger log = LoggerFactory.getLogger(ChaincodeResource.class);

	public ChaincodeResource() {
	}

	/**
	 * POST /blogs/chaincode/add : add a new value to the blockchain.
	 *
	 * @param value
	 *            the hash of the diploma we want to add to the BC
	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
	 *         with status 417 (EXPECTATION_FAILED), or with status 500
	 *         (INTERNAL_SERVER_ERROR)
	 */
	public ResponseEntity<String> addRequest(@RequestParam String entity, String value) {
		if (entity.isEmpty()) {
			log.debug("Empty entity name");
			return new ResponseEntity<String>("EMPTY_ENTITY_NAME", HttpStatus.EXPECTATION_FAILED);
		}
		if (value.isEmpty()) {
			log.debug("Empty value");
			return new ResponseEntity<String>("EMPTY_VALUE", HttpStatus.EXPECTATION_FAILED);
		}

		Add blockchainRequest;
		String transactionID;
		try {
			blockchainRequest = new Add(entity, value);
			blockchainRequest.send();
			transactionID = blockchainRequest.transactionID;
		} catch (A_BlockchainException e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored,e);
			return new ResponseEntity<String>(errored, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored,e);
			return new ResponseEntity<String>(errored, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Create JSON string
		String returned = "{" + '"' + "transactionID" + '"' + ":" + '"' + transactionID + '"' + "}";
		return new ResponseEntity<String>(returned, HttpStatus.OK);
	}

	/**
	 * GET /blogs/chaincode/get : Get an entity value from the blockchain
	 *
	 * @param entity
	 *            the entity to query
	 * @return the ResponseEntity with status 200 (OK) and the value of the entity,
	 *         or with status 417 (EXPECTATION_FAILED), or with status 500
	 *         (INTERNAL_SERVER_ERROR)
	 */
	public ResponseEntity<String> getRequest(@RequestParam String entity) {
		if (entity.isEmpty()) {
			log.debug("Empty entity name");
			return new ResponseEntity<String>("EMPTY_ENTITY_NAME", HttpStatus.EXPECTATION_FAILED);
		}

		String value = null;
		Get get;

		try {
			get = new Get(entity);
			get.send();
			value = get.state;
		} catch (EntityNotFound e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored);

			// Create JSON string
			JsonObject returned = Json.createObjectBuilder().add("entityState", "NOT_FOUND").build();
			return new ResponseEntity<String>(returned.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored);
			return new ResponseEntity<String>(errored, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (value == null) {
			log.debug("The query has failed");
			return new ResponseEntity<String>("QUERY FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		switch (value) {
		case "NOT_FOUND":
			// Create JSON string
			JsonObject returned = Json.createObjectBuilder().add("entityState", "NOT_FOUND").build();
			return new ResponseEntity<String>(returned.toString(), HttpStatus.OK);
		}

		// Create JSON string
		JsonObject returned = Json.createObjectBuilder().add("entityState", value).build();
		return new ResponseEntity<String>(returned.toString(), HttpStatus.OK);
	}

	/**
	 * DELETE /blogs/chaincode/delete : delete an entity from the blockchain.
	 *
	 * @param entity
	 *            to delete from the blockchain
	 * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
	 *         with status 417 (EXPECTATION_FAILED), or with status 500
	 *         (INTERNAL_SERVER_ERROR)
	 */
	public ResponseEntity<String> deleteRequest(@RequestParam String entity) {
		if (entity.isEmpty()) {
			log.debug("Empty entity name");
			return new ResponseEntity<String>("EMPTY_ENTITY_NAME", HttpStatus.EXPECTATION_FAILED);
		}

		Delete blockchainRequest;
		String transactionID;
		try {
			blockchainRequest = new Delete(entity);
			blockchainRequest.send();
			transactionID = blockchainRequest.transactionID;
		} catch (A_BlockchainException e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored);
			return new ResponseEntity<String>(errored, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored);
			return new ResponseEntity<String>(errored, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Create JSON string
		JsonObject returned = Json.createObjectBuilder().add("transactionID", transactionID).build();
		return new ResponseEntity<String>(returned.toString(), HttpStatus.OK);
	}

	/**
	 * POST /blogs/chaincode/set : set an entity in the blockchain.
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
	public ResponseEntity<String> setRequest(@RequestParam String entity, String value) {
		if (entity.isEmpty()) {
			log.debug("Empty entity name");
			return new ResponseEntity<String>("EMPTY_ENTITY_NAME", HttpStatus.EXPECTATION_FAILED);
		}
		if (value.isEmpty()) {
			log.debug("Empty value");
			return new ResponseEntity<String>("EMPTY_VALUE", HttpStatus.EXPECTATION_FAILED);
		}

		Set blockchainRequest;
		String transactionID;
		try {
			blockchainRequest = new Set(entity, value);
			blockchainRequest.send();
			transactionID = blockchainRequest.transactionID;
		} catch (A_BlockchainException e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored,e);
			return new ResponseEntity<String>(errored, HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			String errored = "BLOCKCHAIN ERROR: " + e.toString();
			log.debug(errored,e);
			return new ResponseEntity<String>(errored, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Create JSON string
		JsonObject returned = Json.createObjectBuilder().add("transactionID", transactionID).build();
		return new ResponseEntity<String>(returned.toString(), HttpStatus.OK);
	}

}
