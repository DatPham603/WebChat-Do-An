PGDMP                      }            friend    16.5    16.5     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
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
    deleted boolean
);
    DROP TABLE public.friend;
       public         heap    postgres    false            �          0    16567    friend 
   TABLE DATA           �   COPY public.friend (id, created_by, created_date, last_modified_by, last_modified_at, confirmed, friend_id, user_id, friend_name, user_name, deleted) FROM stdin;
    public          postgres    false    215   �       P           2606    16573    friend friend_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.friend
    ADD CONSTRAINT friend_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.friend DROP CONSTRAINT friend_pkey;
       public            postgres    false    215            �   B  x��ѽn\!��ާp��0�l�"e�Ti`�N�m���Yْ���@�|���\�#
�%��PC��kl���a#$d@�KzB>��FX��]�S��)��<s�tԤ)u`��˕�Q�
yv��k����
���$ 3v_������pw�Η�����&v�Ⳕjm�٠��0:q�ܳ|$���������n
{��{7-̮ ��Î%��h$�u��F�M��d�rU�<dG!Q�i�!F!���8�%�����z�O����~��R�A� C(�Ll:̅�󇬚k�^��h�O4u��ߦ^���8�}}�؟     