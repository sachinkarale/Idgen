package com.weh.idgen.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weh.idgen.controller.exception.UnableToGetSelectorIDException;
import com.weh.idgen.controller.exception.UnableToGetSelectorListException;
import com.weh.idgen.model.GenerateUniqueID;
import com.weh.idgen.service.IDGenService;

/**
 * Controller class is the entry point for the rest call
 * @author BizRuntime
 */
@RestController
public class IDGenController {

	protected static Logger logger = Logger.getLogger(IDGenController.class);

	/**
	 * This method is used to get the unique generated ID for selector
	 * @param caller : caller name
	 * @param selector : select name
	 * @return GenerateUniqueID : generated unique id in String
	 * @throws UnableToGetSelectorIDException
	 */
	@RequestMapping(value = "/getID/{caller}", method = RequestMethod.GET)
	public synchronized GenerateUniqueID getID(
			@PathVariable("caller") String caller,
			@RequestParam(value = "selector", defaultValue = "NULL") String selector)
			throws UnableToGetSelectorIDException {
		IDGenService idGenService = IDGenService.getInstance();
		return idGenService.getSelectorID(caller, selector);
	}

	/**
	 * This method is to get the List of Selectors from selector file.
	 * @return List of Selectors
	 * @throws UnableToGetSelectorListException
	 */
	@RequestMapping("/ListIDSelectors")
	public synchronized String listIDSelectors()
			throws UnableToGetSelectorListException {
		IDGenService idGenService = IDGenService.getInstance();
		return(idGenService.listOfSelector().toString());
	}
}
