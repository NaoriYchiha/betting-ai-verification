//package org.lytvynovych.bettingaiverification.service;
//
//import jakarta.annotation.PostConstruct;
//import org.lytvynovych.bettingaiverification.entity.Bet;
//import org.lytvynovych.bettingaiverification.entity.User;
//import org.lytvynovych.bettingaiverification.repository.BetRepository;
//import org.lytvynovych.bettingaiverification.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class DataInitService {
//
//    private final UserRepository userRepository;
//    private final BetRepository betRepository;
//
//    public DataInitService(UserRepository userRepository,
//                           BetRepository betRepository) {
//        this.userRepository = userRepository;
//        this.betRepository = betRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//
//        if (userRepository.count() > 0) return;
//
//        // =========================
//        // 🔴 1. ЛУДОМАН (опасный юзер)
//        // =========================
//        User addictedUser = new User();
//        addictedUser.setUsername("addicted_user");
//        addictedUser.setEmail("addicted@mail.com");
//        addictedUser.setPassword("123");
//        addictedUser.setBalance(20);
//        addictedUser.setCreatedAt(LocalDateTime.now());
//        userRepository.save(addictedUser);
//
//        // =========================
//        // 🟡 2. ПОДОЗРИТЕЛЬНЫЙ (крупные ставки)
//        // =========================
//        User whaleUser = new User();
//        whaleUser.setUsername("whale_user");
//        whaleUser.setEmail("whale@mail.com");
//        whaleUser.setPassword("123");
//        whaleUser.setBalance(5000);
//        whaleUser.setCreatedAt(LocalDateTime.now());
//        userRepository.save(whaleUser);
//
//        // =========================
//        // 🟢 3. НОРМАЛЬНЫЙ ПОЛЬЗОВАТЕЛЬ
//        // =========================
//        User normalUser = new User();
//        normalUser.setUsername("normal_user");
//        normalUser.setEmail("normal@mail.com");
//        normalUser.setPassword("123");
//        normalUser.setBalance(300);
//        normalUser.setCreatedAt(LocalDateTime.now());
//        userRepository.save(normalUser);
//
//        LocalDateTime now = LocalDateTime.now();
//
//        // =========================
//        // 🔴 addictedUser — много ставок маленьких + хаос
//        // =========================
//        betRepository.saveAll(List.of(
//                createBet(addictedUser, 5, "HOME_WIN", now.minusMinutes(30)),
//                createBet(addictedUser, 10, "AWAY_WIN", now.minusMinutes(25)),
//                createBet(addictedUser, 15, "DRAW", now.minusMinutes(20)),
//                createBet(addictedUser, 8, "HOME_WIN", now.minusMinutes(15)),
//                createBet(addictedUser, 12, "AWAY_WIN", now.minusMinutes(10))
//        ));
//
//        // =========================
//        // 🟡 whaleUser — огромные ставки (подозрение на сговор)
//        // =========================
//        betRepository.saveAll(List.of(
//                createBet(whaleUser, 500, "HOME_WIN", now.minusHours(2)),
//                createBet(whaleUser, 800, "AWAY_WIN", now.minusHours(1)),
//                createBet(whaleUser, 1200, "HOME_WIN", now.minusMinutes(40))
//        ));
//
//        // =========================
//        // 🟢 normalUser — стабильное поведение
//        // =========================
//        betRepository.saveAll(List.of(
//                createBet(normalUser, 20, "HOME_WIN", now.minusDays(1)),
//                createBet(normalUser, 25, "DRAW", now.minusHours(10)),
//                createBet(normalUser, 15, "AWAY_WIN", now.minusHours(5))
//        ));
//    }
//
//    private Bet createBet(User user, double amount, String outcome, LocalDateTime time) {
//        Bet b = new Bet();
//        b.setUser(user);
//        b.setAmount(amount);
//        b.setOutcome(outcome);
//        b.setCreatedAt(time);
//        return b;
//    }
//}
