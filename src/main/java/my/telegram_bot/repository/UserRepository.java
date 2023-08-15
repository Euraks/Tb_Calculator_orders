package my.telegram_bot.repository;

import my.telegram_bot.model.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<BotUser,Long> {

}
