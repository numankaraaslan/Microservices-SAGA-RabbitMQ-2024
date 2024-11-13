package com.aldimbilet.website.config;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent>
{
	RabbitAdmin rabbitAdmin;

	public ApplicationStartup(RabbitAdmin rabbitAdmin)
	{
		this.rabbitAdmin = rabbitAdmin;
	}

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event)
	{
		rabbitAdmin.initialize();
	}
}