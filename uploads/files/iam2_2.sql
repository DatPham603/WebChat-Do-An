PGDMP  .                     }            iam    16.5    16.5     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16423    iam    DATABASE     {   CREATE DATABASE iam WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Vietnamese_Vietnam.1252';
    DROP DATABASE iam;
                postgres    false            �            1259    16528    users    TABLE     _  CREATE TABLE public.users (
    id uuid NOT NULL,
    created_by character varying(255),
    created_date timestamp(6) with time zone,
    last_modified_by character varying(255),
    last_modified_at timestamp(6) with time zone,
    address character varying(255),
    date_of_birth date,
    deleted boolean,
    email character varying(255),
    enabled boolean,
    last_active timestamp(6) with time zone,
    password character varying(255),
    phone_number character varying(255),
    profile_picture character varying(255),
    username character varying(255),
    avatar character varying(255)
);
    DROP TABLE public.users;
       public         heap    postgres    false            �          0    16528    users 
   TABLE DATA           �   COPY public.users (id, created_by, created_date, last_modified_by, last_modified_at, address, date_of_birth, deleted, email, enabled, last_active, password, phone_number, profile_picture, username, avatar) FROM stdin;
    public          postgres    false    219   �       a           2606    16534    users users_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public            postgres    false    219            �   �  x���Oo�0�s��MN^����FaK*4-,E�8vB��m�O� �Ҧ�a�/�[�=oQx���Q��Ca�����2��rVv]��U�c7��
� v���C`P �_�� #������F~\�l�=t��=|�zh{zw��k��t�nPE�Ϯ'_~f�Y���f����,Mͦ;�� ��LRT����ٮ�s����w�7TR����\�%�MH'�,��]�bѲd��f���������fq%1�����|�D���0�Y��p,ݍ�h?���ë�W�V�g����?�otn��δ�v�n�eI�/��R<�KaXq~�-Gv)Ġc�Ca$�I������n`h@1���ꢾ�~�U����%�F�7E\�q:�>ާ�d?߆�YL�Qt���:i�?��{Oa�����A     