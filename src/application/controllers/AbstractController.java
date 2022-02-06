package application.controllers;

import application.IMediator;

/**
 * Abstract controller.
 * 
 * @author Lennart Glauer
 *
 */
public abstract class AbstractController {
	/*
	 * Application mediator.
	 */
	protected IMediator mediator;

	/**
	 * Set mediator.
	 * 
	 * @param mediator
	 */
	public void setMediator(final IMediator mediator) {
		this.mediator = mediator;
	}

	/**
	 * Get mediator.
	 * 
	 * @return
	 */
	public IMediator getMediator() {
		return mediator;
	}
}
