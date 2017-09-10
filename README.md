# sparkProjectTemplate
A [Giter8][g8] template for Scala Spark Projects.

## What this gives you

This template will bootstrap a new spark project with everyone's "favourite" wordcount example (modified for stop words). You can then replace the wordcount example as desired, and customize the Spark components your project needs.


To encourage good software development practice, this starts with a project at 100% code coverage (e.g. one test :p), while its expected for this to decrease, we hope you use the provided [spark-testing-base][stb] library or similar option.

## Using

Have g8 installed? You can run it with:
```bash
g8  holdenk/sparkProjectTemplate --name=projectname --organization=com.my.org --sparkVersion=2.2.0
```

Using sbt (0.13.13+) just do
```bash
sbt new holdenk/sparkProjectTemplate.g8
```

## Related

Want to build your application using the Spark Job Server? The [spark-jobserver.g8][sjsg8] template can help you get started too.

## License

This project is available under your choice of Apache 2 or CC0 1.0.
See <https://www.apache.org/licenses/LICENSE-2.0> or <https://creativecommons.org/publicdomain/zero/1.0/> respectively.
This template is distributed without any warranty.

[g8]: http://www.foundweekends.org/giter8/
[stb]: https://github.com/holdenk/spark-testing-base
[sjsg8]: https://github.com/spark-jobserver/spark-jobserver.g8
