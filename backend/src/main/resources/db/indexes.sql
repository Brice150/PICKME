-- Index supporting the selection screen and the conversations.
--
-- These indexes are not declared with @Index on the entities on purpose: with ddl-auto=update
-- Hibernate resolves columnList against the logical (property) names, which is easy to get wrong
-- and makes the whole application fail to start. Managing them here keeps the boot sequence safe.
--
-- Run once against the pickme database:
--   psql -h localhost -U postgres -d pickme -f indexes.sql

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

-- findDisplayedPicturesByUserIds() and getAllUserNotifications() filter on the owner.
-- Check the column name first, it depends on the naming strategy: \d pictures
CREATE INDEX IF NOT EXISTS idx_pictures_fk_user ON pictures (fk_user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_fk_user ON notifications (fk_user_id);
