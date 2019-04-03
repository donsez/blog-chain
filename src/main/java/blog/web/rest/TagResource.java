
package blog.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import blog.domain.Tag;
import blog.network.networkException.A_BlockchainException;
import blog.network.networkException.EntityNotFound;
import blog.network.request.Add;
import blog.network.request.Delete;
import blog.network.request.Get;
import blog.network.request.Set;
import blog.repository.TagRepository;
import blog.web.rest.errors.BadRequestAlertException;
import blog.web.rest.util.HeaderUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Tag.
 */
@RestController
@RequestMapping("/api")
public class TagResource {

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    private static final String ENTITY_NAME = "tag";

    private final TagRepository tagRepository;

    public TagResource(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * POST /tags : Create a new tag.
     *
     * @param tag the tag to create
     * @return the ResponseEntity with status 201 (Created) and with body the new
     *         tag, or with status 400 (Bad Request) if the tag has already
     *         an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tags")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) throws URISyntaxException {
        log.debug("REST request to save Tag : {}", tag);
        if (tag.getId() != null) {
            throw new BadRequestAlertException("A new tag cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Tag result = tagRepository.save(tag);

        // Process blockchain add request
        log.debug("BLOCKCHAIN ADD: " + tag.getId().toString() + " with the value: " + tag.toString());
        ResponseEntity<String> response = addRequest(tag.getId().toString(), tag.toString());
        log.debug("BLOCKCHAIN ADD RESPONSE: " + response);

        return ResponseEntity.created(new URI("/api/tags/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
    }

    /**
     * PUT /tags : Updates an existing tag.
     *
     * @param tag the tag to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated
     *         tag, or with status 400 (Bad Request) if the tag is not
     *         valid, or with status 500 (Internal Server Error) if the tag
     *         couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tags")
    public ResponseEntity<Tag> updateTag(@RequestBody Tag tag) throws URISyntaxException {
        log.debug("REST request to update Tag : {}", tag);
        if (tag.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        // Process blockchain set request
        log.debug("BLOCKCHAIN UPDATE: " + tag.getId().toString() + " to the value: " + tag.toString());
        ResponseEntity<String> response = setRequest(tag.getId().toString(), tag.toString());
        log.debug("BLOCKCHAIN UPDATE RESPONSE: " + response);

        Tag result = tagRepository.save(tag);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tag.getId().toString()))
                .body(result);
    }

    /**
     * GET /tags : get all the tags.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tags in
     *         body
     */
    @GetMapping("/tags")
    public List<Tag> getAllTags() {
        log.debug("REST request to get all Tags");
        return tagRepository.findAll();
    }

    /**
     * GET /tags/:id : get the "id" tag.
     *
     * @param id the id of the tag to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tag, or
     *         with status 404 (Not Found)
     */
    @GetMapping("/tags/{id}")
    public ResponseEntity<Tag> getRequest(@PathVariable Long id) {
        log.debug("REST request to get Tag : {}", id);
        Optional<Tag> tag = tagRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tag);
    }

    /**
     * DELETE /tags/:id : delete the "id" tag.
     *
     * @param id the id of the tag to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        log.debug("REST request to delete Tag : {}", id);

        // Process blockchain delete request
        log.debug("BLOCKCHAIN DELETE: " + id.toString());
        ResponseEntity<String> response = deleteRequest(id.toString());
        log.debug("BLOCKCHAIN DELETE RESPONSE: " + response);

        tagRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * POST /tags/add : add a new value to the blockchain.
     *
     * @param value the hash of the diploma we want to add to the BC
     * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
     *         with status 417 (EXPECTATION_FAILED), or with status 500
     *         (INTERNAL_SERVER_ERROR)
     */
    @PostMapping("/tags/add")
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
            log.debug(errored);
            return new ResponseEntity<String>(errored, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            String errored = "BLOCKCHAIN ERROR: " + e.toString();
            log.debug(errored);
            return new ResponseEntity<String>(errored, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Create JSON string
        String returned = "{" + '"' + "transactionID" + '"' + ":" + '"' + transactionID + '"' + "}";
        return new ResponseEntity<String>(returned, HttpStatus.OK);
    }

    /**
     * GET /tags/get : Get an entity value from the blockchain
     *
     * @param entity the entity to query
     * @return the ResponseEntity with status 200 (OK) and the value of the entity,
     *         or with status 417 (EXPECTATION_FAILED), or with status 500
     *         (INTERNAL_SERVER_ERROR)
     */
    @GetMapping("/tags/get")
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
            String returned = "{" + '"' + "entityState" + '"' + ":" + '"' + "NOT_FOUND" + '"' + "}";
            return new ResponseEntity<String>(returned, HttpStatus.OK);
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
            String returned = "{" + '"' + "entityState" + '"' + ":" + '"' + "NOT_FOUND" + '"' + "}";
            return new ResponseEntity<String>(returned, HttpStatus.OK);
        }

        // Create JSON string
        String returned = "{" + '"' + "entityState" + '"' + ":" + '"' + value + '"' + "}";
        return new ResponseEntity<String>(returned, HttpStatus.OK);
    }

    /**
     * DELETE /tags/delete : delete an entity from the blockchain.
     *
     * @param entity to delete from the blockchain
     * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
     *         with status 417 (EXPECTATION_FAILED), or with status 500
     *         (INTERNAL_SERVER_ERROR)
     */
    @DeleteMapping("/tags/delete")
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
        String returned = "{" + '"' + "transactionID" + '"' + ":" + '"' + transactionID + '"' + "}";
        return new ResponseEntity<String>(returned, HttpStatus.OK);
    }

    /**
     * POST /tags/set : set an entity in the blockchain.
     *
     *
     * @param entity the entity to add to the blockchain
     * @param value  the value to set the entity to
     * @return the ResponseEntity with status 200 (OK) and the transaction ID, or
     *         with status 417 (EXPECTATION_FAILED), or with status 500
     *         (INTERNAL_SERVER_ERROR)
     */
    @PostMapping("/tags/set")
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
            log.debug(errored);
            return new ResponseEntity<String>(errored, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            String errored = "BLOCKCHAIN ERROR: " + e.toString();
            log.debug(errored);
            return new ResponseEntity<String>(errored, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Create JSON string
        String returned = "{" + '"' + "transactionID" + '"' + ":" + '"' + transactionID + '"' + "}";
        return new ResponseEntity<String>(returned, HttpStatus.OK);
    }

}

