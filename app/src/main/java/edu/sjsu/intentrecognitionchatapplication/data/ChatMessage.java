package edu.sjsu.intentrecognitionchatapplication.data;

import android.graphics.Bitmap;

/**
 * Created by jay on 11/7/17.
 */

public class ChatMessage {
        private String chatText;
        private boolean chatImage;
        private String user;

        public ChatMessage(boolean chatImage,String chatText,String user){
            this.chatImage = chatImage;
            this.chatText = chatText;
            this.user = user;
        }

        public String getChatText() {
            return chatText;
        }

        public void setChatText(String chatText) {
            this.chatText = chatText;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public boolean isChatImage() {
            return chatImage;
        }

        public void setChatImage(boolean chatImage) {
            this.chatImage = chatImage;
        }
}
