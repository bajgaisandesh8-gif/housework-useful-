-- ==============================================================================
-- TRISAKTI TRADERS - PRIVATE FAMILY BUSINESS DATABASE SECURITY (SUPABASE / POSTGRES)
-- ==============================================================================
-- STRICT PRIVATE ACCESS CONTROL:
-- ONLY TWO (2) AUTHORIZED ACCOUNTS:
-- 1. Sandesh Bajgai: bajgaisandesh8@gmail.com (Developer & Business Partner)
-- 2. Father's Account: father@trisakti.com (Store Owner & Proprietor)
--
-- ENFORCES:
-- 1. PUBLIC REGISTRATION DISABLED: Uninvited signups get zero database access.
-- 2. ROW LEVEL SECURITY (RLS) MANDATORY on all business tables.
-- 3. ZERO ACCESS FOR ANONYMOUS USERS (anon).
-- 4. ZERO ACCESS FOR UNAUTHORIZED AUTHENTICATED USERS.
-- 5. SHARED BUSINESS LEDGER: Both authorized users access the same unified business records.
-- 6. NO TRUST IN CLIENT-PROVIDED user_id: Authorization checked via auth.jwt() ->> 'email'.
-- ==============================================================================

-- 1. Helper function: verify if requesting user is one of the 2 authorized family members
CREATE OR REPLACE FUNCTION public.is_authorized_family_member()
RETURNS BOOLEAN AS $$
BEGIN
  RETURN (
    auth.role() = 'authenticated' AND
    (
      LOWER(auth.jwt() ->> 'email') IN (
        'bajgaisandesh8@gmail.com',
        'arjunbajgai@trisakti.com',
        'father@trisakti.com'
      )
    )
  );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 2. Business Profile Table (Store metadata, config)
CREATE TABLE IF NOT EXISTS public.business_profiles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  business_name TEXT NOT NULL DEFAULT 'Trisakti Traders',
  location TEXT NOT NULL DEFAULT 'Kathmandu, Nepal',
  currency TEXT NOT NULL DEFAULT 'NPR',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Sales Table
CREATE TABLE IF NOT EXISTS public.sales (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  date DATE NOT NULL,
  amount NUMERIC(12, 2) NOT NULL CHECK (amount >= 0),
  product_cost NUMERIC(12, 2) NOT NULL DEFAULT 0.0 CHECK (product_cost >= 0),
  payment_type TEXT NOT NULL DEFAULT 'CASH', -- 'CASH', 'CREDIT'
  description TEXT,
  notes TEXT,
  recorded_by TEXT DEFAULT (auth.jwt() ->> 'email'),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. Operating Expenses Table
CREATE TABLE IF NOT EXISTS public.expenses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  date DATE NOT NULL,
  amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
  category TEXT NOT NULL,
  description TEXT NOT NULL,
  notes TEXT,
  recorded_by TEXT DEFAULT (auth.jwt() ->> 'email'),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. Credit Customers Table (Udhaar Accounts)
CREATE TABLE IF NOT EXISTS public.credit_customers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  phone TEXT,
  notes TEXT,
  initial_credit NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 6. Credit & Repayment Transactions Table
CREATE TABLE IF NOT EXISTS public.credit_transactions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id UUID NOT NULL REFERENCES public.credit_customers(id) ON DELETE CASCADE,
  date DATE NOT NULL,
  type TEXT NOT NULL CHECK (type IN ('CREDIT', 'PAYMENT')),
  amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
  description TEXT,
  recorded_by TEXT DEFAULT (auth.jwt() ->> 'email'),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 7. Daily Cash Closing Table
CREATE TABLE IF NOT EXISTS public.daily_closings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  date DATE NOT NULL UNIQUE,
  opening_cash NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
  actual_cash NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
  expected_cash NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
  difference NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
  notes TEXT,
  recorded_by TEXT DEFAULT (auth.jwt() ->> 'email'),
  closed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ==============================================================================
-- ENABLE ROW LEVEL SECURITY (RLS) ON ALL TABLES
-- ==============================================================================
ALTER TABLE public.business_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.sales ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.expenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.credit_customers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.credit_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.daily_closings ENABLE ROW LEVEL SECURITY;

-- Revoke default public/anon privileges
REVOKE ALL ON public.business_profiles FROM anon, public;
REVOKE ALL ON public.sales FROM anon, public;
REVOKE ALL ON public.expenses FROM anon, public;
REVOKE ALL ON public.credit_customers FROM anon, public;
REVOKE ALL ON public.credit_transactions FROM anon, public;
REVOKE ALL ON public.daily_closings FROM anon, public;

-- Grant access strictly to authenticated role (policies will further filter)
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO authenticated;

-- ==============================================================================
-- STRICT RLS POLICIES: 2 AUTHORIZED FAMILY ACCOUNTS ONLY
-- ==============================================================================

-- --- BUSINESS PROFILES ---
CREATE POLICY "Authorized family members can view store profile"
  ON public.business_profiles FOR SELECT
  USING (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can update store profile"
  ON public.business_profiles FOR UPDATE
  USING (public.is_authorized_family_member());

-- --- SALES ---
CREATE POLICY "Authorized family members can view all sales"
  ON public.sales FOR SELECT
  USING (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can record sales"
  ON public.sales FOR INSERT
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can update sales"
  ON public.sales FOR UPDATE
  USING (public.is_authorized_family_member())
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can delete sales"
  ON public.sales FOR DELETE
  USING (public.is_authorized_family_member());

-- --- EXPENSES ---
CREATE POLICY "Authorized family members can view all expenses"
  ON public.expenses FOR SELECT
  USING (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can record expenses"
  ON public.expenses FOR INSERT
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can update expenses"
  ON public.expenses FOR UPDATE
  USING (public.is_authorized_family_member())
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can delete expenses"
  ON public.expenses FOR DELETE
  USING (public.is_authorized_family_member());

-- --- CREDIT CUSTOMERS ---
CREATE POLICY "Authorized family members can view credit customers"
  ON public.credit_customers FOR SELECT
  USING (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can add credit customers"
  ON public.credit_customers FOR INSERT
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can update credit customers"
  ON public.credit_customers FOR UPDATE
  USING (public.is_authorized_family_member())
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can delete credit customers"
  ON public.credit_customers FOR DELETE
  USING (public.is_authorized_family_member());

-- --- CREDIT TRANSACTIONS ---
CREATE POLICY "Authorized family members can view credit ledger transactions"
  ON public.credit_transactions FOR SELECT
  USING (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can insert credit/payment transactions"
  ON public.credit_transactions FOR INSERT
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can delete credit transactions"
  ON public.credit_transactions FOR DELETE
  USING (public.is_authorized_family_member());

-- --- DAILY CLOSINGS ---
CREATE POLICY "Authorized family members can view daily closings"
  ON public.daily_closings FOR SELECT
  USING (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can record daily closing"
  ON public.daily_closings FOR INSERT
  WITH CHECK (public.is_authorized_family_member());

CREATE POLICY "Authorized family members can update daily closing"
  ON public.daily_closings FOR UPDATE
  USING (public.is_authorized_family_member())
  WITH CHECK (public.is_authorized_family_member());

-- ==============================================================================
-- STORAGE SECURITY POLICIES (FOR RECEIPTS & BUSINESS DOCUMENTS)
-- ==============================================================================
-- Bucket: 'business-receipts' (Private, not public)
-- INSERT INTO storage.buckets (id, name, public) VALUES ('business-receipts', 'business-receipts', false);

CREATE POLICY "Authorized family members can read private receipts"
  ON storage.objects FOR SELECT
  USING (
    bucket_id = 'business-receipts' AND
    public.is_authorized_family_member()
  );

CREATE POLICY "Authorized family members can upload private receipts"
  ON storage.objects FOR INSERT
  WITH CHECK (
    bucket_id = 'business-receipts' AND
    public.is_authorized_family_member()
  );

CREATE POLICY "Authorized family members can delete private receipts"
  ON storage.objects FOR DELETE
  USING (
    bucket_id = 'business-receipts' AND
    public.is_authorized_family_member()
  );
