/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import java.io.Serializable;

/**
 *
 * @author Maxime BLAISE
 */
public class Message implements Serializable {

    /**
     * Content of the message.
     */
    private final String content;
    
    /**
     * Name of the sender.
     */
    private final String nameSender;

    /**
     * Color of the message.
     */
    private final String color;

    /**
     * Font of the message.
     */
    private final int font;

    /**
     * Create a message with a specific content. Color and Font by default.
     *
     * @param content Content of the message
     * @param nameSender Name of the sender
     */
    public Message(String content, String nameSender) {
        this.content = content;
        this.nameSender = nameSender;
        color = "#ff0000";
        font = 14;
    }

    /**
     * Create a message with a specific content, color and font.
     *
     * @param content Content of the message
     * @param nameSender Name of the sender
     * @param color Color of the message
     * @param font Font of the message
     */
    public Message(String content, String nameSender, String color, int font) {
        this.content = content;
        this.nameSender = nameSender;
        this.color = color;
        this.font = font;
    }

    public String getContent() {
        return content;
    }

    public String getNameSender() {
        return nameSender;
    }

    public String getColor() {
        return color;
    }

    public int getFont() {
        return font;
    }
    
    

}
