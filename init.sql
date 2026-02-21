--
-- PostgreSQL database dump
--

\restrict njaH8fDZImqO5Jipjgjk0CNJgJMudFYV4oHFY8nSZb767EvXqrz2M4uCedBroMq

-- Dumped from database version 18.1 (Debian 18.1-1.pgdg13+2)
-- Dumped by pg_dump version 18.1 (Debian 18.1-1.pgdg13+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: attendance; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.attendance (
    id uuid NOT NULL,
    "user" uuid NOT NULL,
    meetup uuid NOT NULL,
    confirmed timestamp without time zone
);


ALTER TABLE public.attendance OWNER TO myuser;

--
-- Name: feedback; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.feedback (
    id uuid NOT NULL,
    attendance uuid NOT NULL,
    rating smallint NOT NULL,
    comment character varying(255) NOT NULL
);


ALTER TABLE public.feedback OWNER TO myuser;

--
-- Name: meetup_speaker; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.meetup_speaker (
    meetup uuid NOT NULL,
    speaker uuid NOT NULL
);


ALTER TABLE public.meetup_speaker OWNER TO myuser;

--
-- Name: meetups; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.meetups (
    id uuid NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    start_date timestamp without time zone NOT NULL,
    venue uuid NOT NULL
);


ALTER TABLE public.meetups OWNER TO myuser;

--
-- Name: speakers; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.speakers (
    id uuid NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    biography character varying(255) NOT NULL,
    company character varying(255)
);


ALTER TABLE public.speakers OWNER TO myuser;

--
-- Name: user; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public."user" (
    id uuid NOT NULL,
    nickname character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    avatar character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL
);


ALTER TABLE public."user" OWNER TO myuser;

--
-- Name: venues; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.venues (
    id uuid NOT NULL,
    place character varying(255) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    address character varying(255) NOT NULL,
    seats integer NOT NULL
);


ALTER TABLE public.venues OWNER TO myuser;

--
-- Data for Name: attendance; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.attendance (id, "user", meetup, confirmed) FROM stdin;
51c21b86-f5ae-4b85-b622-fc60eb648b37	c5934ca4-4da6-49cf-a05b-4542de46e762	262868db-67f5-46cb-af9c-d00df06b2d00	2026-01-20 16:47:02.424344
51c21b86-f5ae-4b85-b622-fc60eb648b36	c5934ca4-4da6-49cf-a05b-4542de46e762	8e9de9a6-532c-44ef-be6c-02bf1cb45cea	2026-01-20 16:47:02.424
\.


--
-- Data for Name: feedback; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.feedback (id, attendance, rating, comment) FROM stdin;
b4ab87b6-6b28-4286-a808-c941063ba3c1	51c21b86-f5ae-4b85-b622-fc60eb648b37	5	Hola! El evento ha estado genial y el speaker de 10
ae9b5c53-0be0-4f92-8638-870c00c1047c	51c21b86-f5ae-4b85-b622-fc60eb648b36	5	Me ha encantado el evento y el speaker mas. Volvere de nuevo
\.


--
-- Data for Name: meetup_speaker; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.meetup_speaker (meetup, speaker) FROM stdin;
050d5b69-a620-4919-8629-eb3513168b3b	94e17b18-a26f-4224-a571-cc9221da1f69
8e9de9a6-532c-44ef-be6c-02bf1cb45cea	8920f8c1-a0db-4008-9152-28c0c2f4b1bb
262868db-67f5-46cb-af9c-d00df06b2d00	2ec7b495-0add-48d5-b7d4-a5c3b6d92541
050d5b69-a620-4919-8629-eb3513168b3b	8920f8c1-a0db-4008-9152-28c0c2f4b1bb
\.


--
-- Data for Name: meetups; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.meetups (id, title, description, start_date, venue) FROM stdin;
8e9de9a6-532c-44ef-be6c-02bf1cb45cea	Web3 Developers	Evento sobre desarrollo descentralizado	2025-12-02 17:00:00	1bbbcebf-73db-4bf0-b352-4462e3a5462e
262868db-67f5-46cb-af9c-d00df06b2d00	Cybersecurity 2025	Jornada sobre ciberseguridad y privacidad	2025-12-15 09:30:00	544c899e-f9c5-4bba-b492-13ede3bfc621
050d5b69-a620-4919-8629-eb3513168b3b	3D Printing Day: From bits to Atoms	Charla sobre inteligencia artificial aplicada	2026-11-10 18:00:00	84af9200-8a59-48e7-abc7-377fa9994226
\.


--
-- Data for Name: speakers; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.speakers (id, first_name, last_name, biography, company) FROM stdin;
8920f8c1-a0db-4008-9152-28c0c2f4b1bb	Carlos	Ruíz	Desarrollador Blockchain y formador	CryptoWorks
2ec7b495-0add-48d5-b7d4-a5c3b6d92541	Ana	Torres	Consultora en ciberseguridad	SecureNow
94e17b18-a26f-4224-a571-cc9221da1f69	Laura	Martínez	Android Engineer @ Google	MedTech
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public."user" (id, nickname, email, avatar, create_date) FROM stdin;
9816fd12-6748-4766-9c6b-0c98a686d28a	maso	maso@test.com	https://kogfbxtjiqvbbspgwodz.supabase.co/storage/v1/object/public/avatars/savia.jpg	2026-02-01 19:38:41.028712
c5934ca4-4da6-49cf-a05b-4542de46e762	Masopego	masopego@gmail.com	https://kogfbxtjiqvbbspgwodz.supabase.co/storage/v1/object/public/avatars/03ffd429-edfa-4a63-ac65-246bbee8f12b.jpg	2025-12-21 13:33:22.613803
\.


--
-- Data for Name: venues; Type: TABLE DATA; Schema: public; Owner: myuser
--

COPY public.venues (id, place, latitude, longitude, address, seats) FROM stdin;
84af9200-8a59-48e7-abc7-377fa9994226	Clasijazz	36.8342656	-2.4641421	Calle Maestro Serrano 9, Almería	100
1bbbcebf-73db-4bf0-b352-4462e3a5462e	Coworking Workspace	36.8393027	-2.4737653	Calle Arráez 11, Almería	58
544c899e-f9c5-4bba-b492-13ede3bfc621	Teatro Apolo	36.8406024	-2.4667382	Calle Rambla Obispo Orberá 25, Almería	250
\.


--
-- Name: attendance attendance_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.attendance
    ADD CONSTRAINT attendance_pkey PRIMARY KEY (id);


--
-- Name: feedback feedback_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.feedback
    ADD CONSTRAINT feedback_pkey PRIMARY KEY (id);


--
-- Name: meetups meetups_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.meetups
    ADD CONSTRAINT meetups_pkey PRIMARY KEY (id);


--
-- Name: meetup_speaker pk_meetup_speaker; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.meetup_speaker
    ADD CONSTRAINT pk_meetup_speaker PRIMARY KEY (meetup, speaker);


--
-- Name: speakers speakers_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.speakers
    ADD CONSTRAINT speakers_pkey PRIMARY KEY (id);


--
-- Name: user user_email_unique; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_email_unique UNIQUE (email);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: venues venues_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.venues
    ADD CONSTRAINT venues_pkey PRIMARY KEY (id);


--
-- Name: attendance fk_attendance_meetup__id; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.attendance
    ADD CONSTRAINT fk_attendance_meetup__id FOREIGN KEY (meetup) REFERENCES public.meetups(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: feedback fk_feedback_attendance__id; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.feedback
    ADD CONSTRAINT fk_feedback_attendance__id FOREIGN KEY (attendance) REFERENCES public.attendance(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: meetups fk_meetups_venue__id; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.meetups
    ADD CONSTRAINT fk_meetups_venue__id FOREIGN KEY (venue) REFERENCES public.venues(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- PostgreSQL database dump complete
--

\unrestrict njaH8fDZImqO5Jipjgjk0CNJgJMudFYV4oHFY8nSZb767EvXqrz2M4uCedBroMq

