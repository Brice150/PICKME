-- Indexes supporting the selection screen and the conversations.

-- getAllUsers() joins likes and dislikes on the connected user to exclude the profiles already
-- answered. Without these, every call to the selection screen scans both tables in full.
CREATE INDEX IF NOT EXISTS idx_likes_fk_sender ON likes (fk_sender);
CREATE INDEX IF NOT EXISTS idx_likes_fk_receiver ON likes (fk_receiver);
CREATE INDEX IF NOT EXISTS idx_dislikes_fk_sender ON dislikes (fk_sender);
CREATE INDEX IF NOT EXISTS idx_dislikes_fk_receiver ON dislikes (fk_receiver);

-- getUserMessagesByFk() and deleteMessagesByFk() filter on both sides of a conversation.
CREATE INDEX IF NOT EXISTS idx_messages_fk_sender ON messages (fk_sender);
CREATE INDEX IF NOT EXISTS idx_messages_fk_receiver ON messages (fk_receiver);

-- Login and every getConnectedUser() call look the account up by email.
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

-- The selection screen filters the candidates on a range of birth dates.
CREATE INDEX IF NOT EXISTS idx_users_birth_date ON users (birth_date);

-- findDisplayedPicturesByUserIds() and getAllUserNotifications() filter on the owner.
CREATE INDEX IF NOT EXISTS idx_pictures_fk_user ON pictures (fk_user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_fk_user ON notifications (fk_user_id);
