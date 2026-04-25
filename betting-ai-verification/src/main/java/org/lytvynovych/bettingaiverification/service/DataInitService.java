//package org.lytvynovych.bettingaiverification.service;
//
//import jakarta.annotation.PostConstruct;
//import org.lytvynovych.bettingaiverification.entity.Bet;
//import org.lytvynovych.bettingaiverification.entity.Match;
//import org.lytvynovych.bettingaiverification.entity.User;
//import org.lytvynovych.bettingaiverification.repository.BetRepository;
//import org.lytvynovych.bettingaiverification.repository.MatchRepository;
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
//    private final MatchRepository matchRepository;
//
//    public DataInitService(UserRepository userRepository,
//                           BetRepository betRepository,
//                           MatchRepository matchRepository) {
//        this.userRepository = userRepository;
//        this.betRepository = betRepository;
//        this.matchRepository = matchRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//        if (userRepository.count() > 0) return;
//
//        // берём матчи по конкретным ID которые есть в БД
//        Match m1  = match(1L);
//        Match m2  = match(2L);
//        Match m3  = match(3L);
//        Match m4  = match(4L);
//        Match m5  = match(5L);
//        Match m6  = match(6L);
//        Match m7  = match(7L);
//        Match m8  = match(8L);
//        Match m9  = match(52L);
//        Match m10 = match(53L);
//        Match m11 = match(54L);
//        Match m12 = match(55L);
//        Match m13 = match(56L);
//        Match m14 = match(57L);
//
//        if (m1 == null) {
//            System.out.println("⚠ Matches not found in DB, skipping DataInitService");
//            return;
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//
//        // ============================================================
//        // 🔴 USER 1 — ЛУДОМАН: много хаотичных ставок, маленькие суммы
//        // ============================================================
//        User addicted = save("addicted_user", "addicted@mail.com", 20, false);
//        bets(List.of(
//                bet(addicted, m1,  5,  "HOME_WIN",  now.minusMinutes(30)),
//                bet(addicted, m2,  10, "AWAY_WIN",  now.minusMinutes(25)),
//                bet(addicted, m3,  15, "DRAW",      now.minusMinutes(20)),
//                bet(addicted, m4,  8,  "HOME_WIN",  now.minusMinutes(15)),
//                bet(addicted, m5,  12, "AWAY_WIN",  now.minusMinutes(10)),
//                bet(addicted, m6,  7,  "DRAW",      now.minusMinutes(5)),
//                bet(addicted, m7,  9,  "HOME_WIN",  now.minusMinutes(2))
//        ));
//
//        // ============================================================
//        // 🟡 USER 2 — КИТ: огромные ставки, подозрение на сговор
//        // ============================================================
//        User whale = save("whale_user", "whale@mail.com", 5000, false);
//        bets(List.of(
//                bet(whale, m1,  500,  "HOME_WIN",  now.minusHours(2)),
//                bet(whale, m2,  800,  "AWAY_WIN",  now.minusHours(1)),
//                bet(whale, m3,  1200, "HOME_WIN",  now.minusMinutes(40)),
//                bet(whale, m4,  900,  "HOME_WIN",  now.minusMinutes(20))
//        ));
//
//        // ============================================================
//        // 🟢 USER 3 — НОРМАЛЬНЫЙ: стабильное поведение
//        // ============================================================
//        User normal = save("normal_user", "normal@mail.com", 300, false);
//        bets(List.of(
//                bet(normal, m1, 20, "HOME_WIN",  now.minusDays(1)),
//                bet(normal, m2, 25, "DRAW",      now.minusHours(10)),
//                bet(normal, m3, 15, "AWAY_WIN",  now.minusHours(5))
//        ));
//
//        // ============================================================
//        // 🔴 USER 4 — ФИКСЕР: всегда ставит на аутсайдера
//        // ============================================================
//        User fixer = save("match_fixer", "fixer@mail.com", 9999, false);
//        bets(List.of(
//                bet(fixer, m1,  500, "AWAY_WIN", now.minusDays(3)),
//                bet(fixer, m2,  500, "AWAY_WIN", now.minusDays(2)),
//                bet(fixer, m3,  500, "AWAY_WIN", now.minusDays(1)),
//                bet(fixer, m9,  500, "AWAY_WIN", now.minusHours(6)),
//                bet(fixer, m10, 500, "AWAY_WIN", now.minusHours(3)),
//                bet(fixer, m11, 500, "AWAY_WIN", now.minusHours(1))
//        ));
//
//        // ============================================================
//        // 🔴 USER 5 — ЗАБЛОКИРОВАННЫЙ
//        // ============================================================
//        User banned = save("banned_user", "banned@mail.com", 0, true);
//        bets(List.of(
//                bet(banned, m1, 300, "HOME_WIN", now.minusDays(5)),
//                bet(banned, m2, 300, "HOME_WIN", now.minusDays(4)),
//                bet(banned, m3, 300, "HOME_WIN", now.minusDays(3))
//        ));
//
//        // ============================================================
//        // 🟡 USER 6 — МАРТИНГЕЙЛ: удваивает после проигрыша
//        // ============================================================
//        User martingale = save("martingale_mike", "mike@mail.com", 150, false);
//        bets(List.of(
//                bet(martingale, m1,  50,  "HOME_WIN", now.minusDays(7)),
//                bet(martingale, m2,  100, "HOME_WIN", now.minusDays(6)),
//                bet(martingale, m3,  200, "HOME_WIN", now.minusDays(5)),
//                bet(martingale, m4,  400, "HOME_WIN", now.minusDays(4)),
//                bet(martingale, m5,  800, "HOME_WIN", now.minusDays(3)),
//                bet(martingale, m6, 1600, "HOME_WIN", now.minusDays(2)),
//                bet(martingale, m7, 3200, "HOME_WIN", now.minusDays(1))
//        ));
//
//        // ============================================================
//        // 🟢 USER 7 — СЛУЧАЙНЫЙ: разные матчи, суммы, исходы
//        // ============================================================
//        User casual = save("casual_bettor", "casual@mail.com", 800, false);
//        bets(List.of(
//                bet(casual, m5,  30, "HOME_WIN",  now.minusDays(10)),
//                bet(casual, m8,  45, "DRAW",      now.minusDays(7)),
//                bet(casual, m11, 25, "AWAY_WIN",  now.minusDays(4)),
//                bet(casual, m13, 50, "HOME_WIN",  now.minusDays(1))
//        ));
//
//        // ============================================================
//        // 🔴 USER 8 — СПАМЕР: десятки ставок за час
//        // ============================================================
//        User spammer = save("bet_spammer", "spammer@mail.com", 10, false);
//        bets(List.of(
//                bet(spammer, m1, 5,  "HOME_WIN",  now.minusMinutes(60)),
//                bet(spammer, m2, 5,  "AWAY_WIN",  now.minusMinutes(55)),
//                bet(spammer, m3, 5,  "DRAW",      now.minusMinutes(50)),
//                bet(spammer, m4, 5,  "HOME_WIN",  now.minusMinutes(45)),
//                bet(spammer, m5, 5,  "AWAY_WIN",  now.minusMinutes(40)),
//                bet(spammer, m6, 5,  "HOME_WIN",  now.minusMinutes(35)),
//                bet(spammer, m7, 5,  "DRAW",      now.minusMinutes(30)),
//                bet(spammer, m8, 5,  "HOME_WIN",  now.minusMinutes(25)),
//                bet(spammer, m9, 5,  "AWAY_WIN",  now.minusMinutes(20))
//        ));
//
//        // ============================================================
//        // 🟢 USER 9 — АНАЛИТИК: редкие но крупные ставки
//        // ============================================================
//        User analyst = save("smart_analyst", "analyst@mail.com", 2000, false);
//        bets(List.of(
//                bet(analyst, m3,  200, "HOME_WIN", now.minusDays(14)),
//                bet(analyst, m7,  150, "DRAW",     now.minusDays(7)),
//                bet(analyst, m12, 300, "HOME_WIN", now.minusDays(3))
//        ));
//
//        // ============================================================
//        // 🟡 USER 10 — НОВИЧОК: 1 ставка
//        // ============================================================
//        User newbie = save("newbie_player", "newbie@mail.com", 100, false);
//        bets(List.of(
//                bet(newbie, m14, 10, "HOME_WIN", now.minusHours(1))
//        ));
//
//        // ============================================================
//        // 🔴 USER 11 — ПАНИКЁР: резко растущие ставки
//        // ============================================================
//        User panic = save("panic_bettor", "panic@mail.com", 5, false);
//        bets(List.of(
//                bet(panic, m1,  10,   "HOME_WIN",  now.minusDays(5)),
//                bet(panic, m2,  50,   "HOME_WIN",  now.minusDays(4)),
//                bet(panic, m3,  200,  "HOME_WIN",  now.minusDays(3)),
//                bet(panic, m4,  500,  "HOME_WIN",  now.minusDays(2)),
//                bet(panic, m5,  2000, "HOME_WIN",  now.minusDays(1)),
//                bet(panic, m6,  5000, "HOME_WIN",  now.minusHours(3))
//        ));
//
//        // ============================================================
//        // 🟢 USER 12 — КОНСЕРВАТОР: только ничьи, маленькие суммы
//        // ============================================================
//        User conservative = save("draw_lover", "draw@mail.com", 600, false);
//        bets(List.of(
//                bet(conservative, m1, 20, "DRAW", now.minusDays(10)),
//                bet(conservative, m3, 20, "DRAW", now.minusDays(7)),
//                bet(conservative, m5, 20, "DRAW", now.minusDays(4)),
//                bet(conservative, m7, 20, "DRAW", now.minusDays(2)),
//                bet(conservative, m9, 20, "DRAW", now.minusDays(1))
//        ));
//
//        // ============================================================
//        // 🟡 USER 13 — КРУПНЫЙ АУТСАЙДЕР: всегда на аутсайдера, большие суммы
//        // ============================================================
//        User underdog = save("underdog_hunter", "underdog@mail.com", 500, false);
//        bets(List.of(
//                bet(underdog, m2,  300, "AWAY_WIN", now.minusDays(6)),
//                bet(underdog, m4,  300, "AWAY_WIN", now.minusDays(4)),
//                bet(underdog, m6,  300, "AWAY_WIN", now.minusDays(2)),
//                bet(underdog, m10, 300, "AWAY_WIN", now.minusHours(12)),
//                bet(underdog, m12, 300, "AWAY_WIN", now.minusHours(4))
//        ));
//
//        // ============================================================
//        // 🟢 USER 14 — СЕЗОННЫЙ: ставит раз в неделю
//        // ============================================================
//        User seasonal = save("weekend_bettor", "weekend@mail.com", 400, false);
//        bets(List.of(
//                bet(seasonal, m1,  40, "HOME_WIN", now.minusDays(21)),
//                bet(seasonal, m5,  35, "DRAW",     now.minusDays(14)),
//                bet(seasonal, m9,  50, "HOME_WIN", now.minusDays(7))
//        ));
//
//        // ============================================================
//        // 🔴 USER 15 — СИНДИКАТ: несколько больших ставок на один исход
//        // ============================================================
//        User syndicate = save("syndicate_acc", "syndicate@mail.com", 3000, false);
//        bets(List.of(
//                bet(syndicate, m13, 1000, "AWAY_WIN", now.minusDays(1)),
//                bet(syndicate, m13, 1000, "AWAY_WIN", now.minusHours(20)),
//                bet(syndicate, m13, 1000, "AWAY_WIN", now.minusHours(15)),
//                bet(syndicate, m13, 1000, "AWAY_WIN", now.minusHours(10))
//        ));
//
//        // ============================================================
//        // 🟢 USER 16 — СТУДЕНТ: маленькие ставки, разные исходы
//        // ============================================================
//        User student = save("student_gamer", "student@mail.com", 50, false);
//        bets(List.of(
//                bet(student, m3,  5,  "HOME_WIN",  now.minusDays(3)),
//                bet(student, m7,  5,  "DRAW",      now.minusDays(2)),
//                bet(student, m11, 10, "AWAY_WIN",  now.minusDays(1))
//        ));
//
//        // ============================================================
//        // 🟡 USER 17 — ПРОФИ: систематические ставки средних размеров
//        // ============================================================
//        User pro = save("pro_bettor", "pro@mail.com", 1200, false);
//        bets(List.of(
//                bet(pro, m2,  100, "HOME_WIN",  now.minusDays(8)),
//                bet(pro, m4,  120, "AWAY_WIN",  now.minusDays(6)),
//                bet(pro, m6,  90,  "HOME_WIN",  now.minusDays(4)),
//                bet(pro, m8,  110, "DRAW",      now.minusDays(3)),
//                bet(pro, m10, 130, "HOME_WIN",  now.minusDays(2)),
//                bet(pro, m12, 95,  "AWAY_WIN",  now.minusDays(1)),
//                bet(pro, m14, 105, "HOME_WIN",  now.minusHours(5))
//        ));
//
//        // ============================================================
//        // 🔴 USER 18 — БАНКРОТ: баланс 0, продолжает ставить
//        // ============================================================
//        User bankrupt = save("broke_gambler", "broke@mail.com", 0, false);
//        bets(List.of(
//                bet(bankrupt, m1, 500, "HOME_WIN",  now.minusDays(2)),
//                bet(bankrupt, m2, 500, "HOME_WIN",  now.minusDays(1)),
//                bet(bankrupt, m3, 500, "HOME_WIN",  now.minusHours(6)),
//                bet(bankrupt, m4, 500, "HOME_WIN",  now.minusHours(3)),
//                bet(bankrupt, m5, 500, "HOME_WIN",  now.minusHours(1))
//        ));
//
//        // ============================================================
//        // 🟢 USER 19 — ОСТОРОЖНЫЙ: 2 ставки за всё время
//        // ============================================================
//        User careful = save("careful_carl", "carl@mail.com", 750, false);
//        bets(List.of(
//                bet(careful, m6,  50, "HOME_WIN", now.minusDays(30)),
//                bet(careful, m11, 75, "DRAW",     now.minusDays(15))
//        ));
//
//        // ============================================================
//        // 🟡 USER 20 — ЭМОЦИОНАЛЬНЫЙ: после проигрыша ставит больше
//        // ============================================================
//        User emotional = save("tilt_player", "tilt@mail.com", 200, false);
//        bets(List.of(
//                bet(emotional, m1,  30,  "HOME_WIN",  now.minusDays(4)),
//                bet(emotional, m2,  30,  "HOME_WIN",  now.minusDays(4).plusHours(1)),
//                bet(emotional, m3,  100, "HOME_WIN",  now.minusDays(4).plusHours(2)),
//                bet(emotional, m4,  100, "HOME_WIN",  now.minusDays(4).plusHours(3)),
//                bet(emotional, m5,  400, "HOME_WIN",  now.minusDays(4).plusHours(4)),
//                bet(emotional, m6,  400, "HOME_WIN",  now.minusDays(4).plusHours(5)),
//                bet(emotional, m7, 1500, "HOME_WIN",  now.minusDays(4).plusHours(6)),
//                bet(emotional, m8, 1500, "HOME_WIN",  now.minusDays(4).plusHours(7))
//        ));
//
//        // ============================================================
//        // 🟢 USER 21 — VIP: крупный но стабильный
//        // ============================================================
//        User vip = save("vip_player", "vip@mail.com", 50000, false);
//        bets(List.of(
//                bet(vip, m9,  1000, "HOME_WIN",  now.minusDays(10)),
//                bet(vip, m10, 1500, "DRAW",      now.minusDays(7)),
//                bet(vip, m11, 800,  "HOME_WIN",  now.minusDays(5)),
//                bet(vip, m12, 1200, "AWAY_WIN",  now.minusDays(3)),
//                bet(vip, m13, 900,  "HOME_WIN",  now.minusDays(1))
//        ));
//
//        System.out.println("✅ DataInitService: 21 users and bets successfully created");
//    }
//
//    // helpers
//    private User save(String username, String email, double balance, boolean blocked) {
//        User u = new User();
//        u.setUsername(username);
//        u.setEmail(email);
//        u.setPassword("hashed_password");
//        u.setBalance(balance);
//        u.setBlocked(blocked);
//        u.setCreatedAt(LocalDateTime.now());
//        return userRepository.save(u);
//    }
//
//    private Bet bet(User user, Match match, double amount, String outcome, LocalDateTime time) {
//        Bet b = new Bet();
//        b.setUser(user);
//        b.setMatch(match);
//        b.setAmount(amount);
//        b.setOutcome(outcome);
//        b.setCreatedAt(time);
//        return b;
//    }
//
//    private void bets(List<Bet> bets) {
//        betRepository.saveAll(bets);
//    }
//
//    private Match match(Long id) {
//        return matchRepository.findById(id).orElse(null);
//    }
//}
