package com.example.session.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

	private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

	private final DataSource dataSource;

	@PostMapping(path = "/close", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void closeSession(@RequestBody String username) {
		var userSessions = sessionRepository.findByPrincipalName(username);
		userSessions.values().forEach(session -> sessionRepository.deleteById(session.getId()));
		log.info(username + "'s session closed");
	}

	@GetMapping(path = "/users")
	public List<String> connectedUsers() {
		try {
			try (Connection connection = dataSource.getConnection()) {
				var nativeSQL = connection.nativeSQL("select distinct PRINCIPAL_NAME from SPRING_SESSION");
				var rs = connection.prepareStatement(nativeSQL).executeQuery();
				var result = new ArrayList<String>();
				while (rs.next())
					result.add(rs.getString(1));
				return result;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
