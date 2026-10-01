package tercacost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        
        // Configurações forçadas via código para blindar o DataSource contra bugs de cache da IDE
        Map<String, Object> propriedadesMySql = new HashMap<>();
        
        // REVISE SE O NOME DO BANCO NO WORKBENCH É EXATAMENTE: terca_cost_bd
        propriedadesMySql.put("spring.datasource.url", "jdbc:mysql://localhost:3306/terca_cost_bd?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        
        // SUBSTITUA ABAIXO PELO SEU USUÁRIO E SENHA REAIS DO WORKBENCH (Geralmente o user é root)
        propriedadesMySql.put("spring.datasource.username", "root");
        propriedadesMySql.put("spring.datasource.password", "@1@senac2021");
        
        propriedadesMySql.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        propriedadesMySql.put("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect");
        propriedadesMySql.put("spring.jpa.hibernate.ddl-auto", "update");


        propriedadesMySql.put("spring.jpa.show-sql", "true");

        app.setDefaultProperties(propriedadesMySql);
        app.run(args);
    }
}
