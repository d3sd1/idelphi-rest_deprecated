package com.uc3m.delphi.ws.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

public class WebsocketErrorHandler extends StompSubProtocolErrorHandler {/*
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]>clientMessage, Throwable ex)
    {
        Throwable exception = ex;
        if (exception instanceof MessageDeliveryException)
        {
            exception = exception.getCause();
        }

        if (exception instanceof UnauthorizedException)
        {
            return handleUnauthorizedException(clientMessage, exception);
        }

        if (exception instanceof AccessDeniedException)
        {
            return handleAccessDeniedException(clientMessage, exception);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage, Throwable ex)
    {
        ApiError apiError = new ApiError(ErrorCodeConstants.UNAUTHORIZED, ex.getMessage());

        return prepareErrorMessage(clientMessage, apiError, ErrorCodeConstants.UNAUTHORIZED_STRING);

    }

    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, String message, String errorCode)
    {

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        //setReceiptIdForClient(clientMessage, accessor);
        accessor.setMessage(errorCode);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message != null ? message.getBytes() : null, accessor.getMessageHeaders());
    }*/
}
