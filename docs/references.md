# References LaTeX Block

Use this block if the thesis uses `thebibliography`. To make the reference title clickable while hiding the full URL, add this package to the LaTeX preamble:

```latex
\usepackage{hyperref}
```

Then copy this reference block:

```latex
\begin{thebibliography}{99}

\bibitem{hacom}
HACOM, \href{https://hacom.vn/buildpc}{``Build PC Online.''} Accessed: Jun. 29, 2026.

\bibitem{gearvn}
GEARVN, \href{https://gearvn.com/pages/build-pc}{``Build PC.''} Accessed: Jun. 29, 2026.

\bibitem{schneeweiss2011}
D. Schneeweiss and P. Hofstedt, \href{https://arxiv.org/abs/1108.5586}{``FdConfig: A Constraint-Based Interactive Product Configurator,''} arXiv:1108.5586, 2011.

\bibitem{le2023constraint}
N. L. Le, M.-H. Abel, and P. Gouspillou, \href{https://arxiv.org/abs/2307.10702}{``A Constraint-based Recommender System via RDF Knowledge Graphs,''} arXiv:2307.10702, 2023.

\bibitem{dutta2011}
R. Dutta and D. Mukhopadhyay, \href{https://arxiv.org/abs/1109.4257}{``Offering A Product Recommendation System in E-commerce,''} arXiv:1109.4257, 2011.

\bibitem{felfernig2021}
A. Felfernig, S. Reiterer, M. Stettinger, F. Reinfrank, M. Jeran, and G. Ninaus, \href{https://arxiv.org/abs/2102.08113}{``Recommender Systems for Configuration Knowledge Engineering,''} arXiv:2102.08113, 2021.

\bibitem{springboot}
Spring, \href{https://docs.spring.io/spring-boot/}{``Spring Boot Reference Documentation.''} Accessed: Jun. 29, 2026.

\bibitem{springsecurity}
Spring, \href{https://docs.spring.io/spring-security/reference/}{``Spring Security Reference Documentation.''} Accessed: Jun. 29, 2026.

\bibitem{thymeleaf}
Thymeleaf, \href{https://www.thymeleaf.org/documentation.html}{``Thymeleaf Documentation.''} Accessed: Jun. 29, 2026.

\bibitem{springdatajpa}
Spring, \href{https://docs.spring.io/spring-data/jpa/reference/}{``Spring Data JPA Reference Documentation.''} Accessed: Jun. 29, 2026.

\bibitem{mysql}
Oracle, \href{https://dev.mysql.com/doc/}{``MySQL Documentation.''} Accessed: Jun. 29, 2026.

\bibitem{flyway}
Redgate, \href{https://documentation.red-gate.com/flyway}{``Flyway Documentation.''} Accessed: Jun. 29, 2026.

\bibitem{docker}
Docker, \href{https://docs.docker.com/}{``Docker Documentation.''} Accessed: Jun. 29, 2026.

\bibitem{payos}
PayOS, \href{https://docs.payos.vn/}{``PayOS Documentation.''} Accessed: Jun. 29, 2026.

\end{thebibliography}
```

## Notes

The links are hidden behind the reference titles. For example, clicking `Build PC Online` opens the HACOM Build PC page. If the PDF does not make links clickable, check whether `\usepackage{hyperref}` is included in the main LaTeX file.
