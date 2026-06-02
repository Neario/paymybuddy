CREATE DATABASE IF NOT EXISTS paymybuddy;
USE paymybuddy;

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL UNIQUE,
  `balance` int NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_wallet_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `user_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `relation_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_relation_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_relation_contact` FOREIGN KEY (`relation_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

CREATE TABLE `transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `amount` int NOT NULL,  
  `fee` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_transaction_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_transaction_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
);