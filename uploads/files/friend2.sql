PGDMP  )    
                }            friend    16.5    16.5     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16566    friend    DATABASE     ~   CREATE DATABASE friend WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Vietnamese_Vietnam.1252';
    DROP DATABASE friend;
                postgres    false            �            1259    16567    friend    TABLE     �  CREATE TABLE public.friend (
    id uuid NOT NULL,
    created_by character varying(255),
    created_date timestamp(6) with time zone,
    last_modified_by character varying(255),
    last_modified_at timestamp(6) with time zone,
    confirmed boolean,
    friend_id uuid,
    user_id uuid,
    friend_name character varying(255),
    user_name character varying(255),
    deleted boolean,
    email character varying(255),
    friend_email character varying(255)
);
    DROP TABLE public.friend;
       public         heap    postgres    false            �          0    16567    friend 
   TABLE DATA           �   COPY public.friend (id, created_by, created_date, last_modified_by, last_modified_at, confirmed, friend_id, user_id, friend_name, user_name, deleted, email, friend_email) FROM stdin;
    public          postgres    false    215          O           2606    16573    friend friend_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.friend
    ADD CONSTRAINT friend_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.friend DROP CONSTRAINT friend_pkey;
       public            postgres    false    215            �   �  x���=nA��SLnը����{"�H "��e-1k$O�-8\�.�Mh�	�f��.=��}�5e���D�[Y!F(�l.Sb7<|y��:���4Y�.��2���s����م�� �4�`C�>��S߇�A,��TmJ��n����J)`�z԰�%���)�����u����������������0���_�І�ς����>��~=M�|2fomp�����B1��R�t��1��Ut/��,�^�A��)5lP���'�D����������}<N�q{ʣ�k�4a	r��.�����u��}T��hrԲ��=p�+�����Ŀг��@�N/�[�a�j���)j$m=�~EI,9K��|�J^��Z׋��}W�w�~�������8� p��     