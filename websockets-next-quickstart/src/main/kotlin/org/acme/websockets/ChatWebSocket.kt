package org.acme.websockets

import io.quarkus.websockets.next.OnClose
import io.quarkus.websockets.next.OnOpen
import io.quarkus.websockets.next.OnTextMessage
import io.quarkus.websockets.next.WebSocket
import io.quarkus.websockets.next.WebSocketConnection

@WebSocket(path = "/chat/{username}")
class ChatWebSocket(
    val connection: WebSocketConnection,
) {
    // Declare the type of messages that can be sent and received
    enum class MessageType {
        USER_JOINED, USER_LEFT, CHAT_MESSAGE
    }

    @JvmRecord
    data class ChatMessage(val type: MessageType, val from: String, val message: String?)

    @OnOpen(broadcast = true)
    fun onOpen(): ChatMessage {
        return ChatMessage(MessageType.USER_JOINED, connection.pathParam("username"), null)
    }

    @OnClose
    fun onClose() {
        val departure = ChatMessage(MessageType.USER_LEFT, connection.pathParam("username"), null)
        connection.broadcast().sendTextAndAwait(departure)
    }

    @OnTextMessage(broadcast = true)
    fun onMessage(message: ChatMessage): ChatMessage {
        return message
    }
}