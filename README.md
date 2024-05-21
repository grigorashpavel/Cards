# Cards
Common:
1. Мне нужно сделать Retrofit + Interceptor + Authentificator - работа с токенами
2. Мне нужно сделать функционал создания карт - определиться с наполненостью, а для того что я хочу вообще нужно CustomeView
3. Создать Account, после сделать синхронизацию данных - мне нечего синхрониизировать


Верхнеуровневые зависимости - которые не нужны другим, но м нужны другие
Хорошая практика - в компоненте верхнеуровневые зависимости, а в модулях остальные

Связывают модули через includes: @Module(includes = )
Связывают компоненты и модули через modules: @Component(modules = {...})

Аннотация @Scope говорит Dagger 2 создавать только единственный экземпляр
@Retention — это аннотация для обозначения точки отклонения использования аннотации. 
Она говорит о том, когда может быть использована аннотация. Например, 
с отметкой SOURCE аннотация будет доступна только в исходном коде и будет отброшена во время компиляции, 
с отметкой CLASS аннотация будет доступна во время компиляции, но не во время работы программы, 
с отметкой RUNTIME аннотация будет доступна и во время выполнения программы.

@Scope
@Retention(RetentionPolicy.CLASS)
    public @interface RandomUserApplicationScope {
}


Альтернатива аннотации @Named — @Qualifier
Для замены аннотации @Named на @Qualifier нужно создать отдельную аннотацию и использовать её где необходимо.


@Component(dependencies = RandomUserComponent.class)
@MainActivityScope
public interface MainActivityComponent {
    RandomUserAdapter getRandomUserAdapter();
    RandomUsersApi getRandomUserService();
}

Необходимо позволить MainActivityComponent ссылаться на RandomUserComponent, 
для чего используется атрибут dependencies. Другими словами, этот атрибут говорит Dagger 2 
обращаться к RandomUserComponent, если требуются дополнительные зависимости.
